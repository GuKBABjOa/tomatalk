/*
이 모듈은 사용자의 오디오를 캡처하고 PCM 데이터를 생성한 후, 지정된 콜백함수를 호출합니다.
콜백에서 전송전에 추가 메타데이터를 병합할 수 있습니다.
*/
import { ref } from "vue";

export function useAudioStreaming() {
    const audioContext = ref<AudioContext | null>(null);
    const mediaStream = ref<MediaStream | null>(null);
    let pcmProcessor: AudioWorkletNode | null = null;

    // ArrayBuffer를 Base64 문자열로 변환하는 유틸 함수
    function arrayBufferToBase64(buffer: ArrayBuffer): string {
        let binary = "";
        const bytes = new Uint8Array(buffer);
        for (let i = 0; i < bytes.byteLength; i++) {
            binary += String.fromCharCode(bytes[i]);
        }
        return window.btoa(binary);
    }

    // 오디오 캡처 및 AudioWorkletProcessor를 동적으로 로드하여 PCM 데이터를 생성하고, onData 콜백 호출
    async function startStreaming(onData: (data: any) => void) {
        if (audioContext.value) return; // 이미 실행 중인 경우
        console.log("Audio streaming 시작 시도...");
        audioContext.value = new AudioContext({ sampleRate: 16000 });
        mediaStream.value = await navigator.mediaDevices.getUserMedia({ audio: true });
        console.log("Media stream 가져옴:", mediaStream.value);
        const source = audioContext.value.createMediaStreamSource(mediaStream.value);
        console.log("AudioWorklet 모듈 추가 완료");
        const workletCode = `
      class PCMProcessor extends AudioWorkletProcessor {
        process(inputs, outputs, parameters) {
          if (inputs.length > 0 && inputs[0].length > 0) {
            const input = inputs[0][0]; // 첫 번째 채널의 데이터
            const pcmData = new Int16Array(input.length);
            let sum = 0;
            for (let i = 0; i < input.length; i++) {
              let sample = Math.max(-1, Math.min(1, input[i]));
              if (isNaN(sample) || !isFinite(sample)) sample = 0;
              pcmData[i] = Math.floor(sample * 32768);
              sum += Math.abs(pcmData[i]);
            }
            // 무음이 아닌 경우에만 전송 (예: sum이 일정 값 이상이면)
            if (sum > 100) {
              // Worklet에서 간단한 메타데이터 생성 (예, 타임스탬프)
              const metadata = { timestamp: Date.now() };
              this.port.postMessage({ metadata, audioBuffer: pcmData.buffer });
            }
          }
          return true;
        }
      }
      registerProcessor("pcm-processor", PCMProcessor);
    `;
        const blob = new Blob([workletCode], { type: "application/javascript" });
        const workletUrl = URL.createObjectURL(blob);
        await audioContext.value.audioWorklet.addModule(workletUrl);
        pcmProcessor = new AudioWorkletNode(audioContext.value, "pcm-processor");

        // Worklet이 PCM 데이터를 수신하면 onData 콜백 호출
        pcmProcessor.port.onmessage = (event) => {
            console.log("PCM 데이터 수신:", event.data);
            const payload = event.data; // { metadata, audioBuffer }
            const base64Audio = arrayBufferToBase64(payload.audioBuffer);
            // dataToSend에는 PCM 데이터(인코딩된 문자열)와 Worklet에서 보낸 메타데이터가 포함됨
            const dataToSend = { metadata: payload.metadata, audioData: base64Audio };
            onData(dataToSend);
        };

        source.connect(pcmProcessor);
    }

    function stopStreaming() {
        if (mediaStream.value) {
            mediaStream.value.getTracks().forEach((track) => track.stop());
            mediaStream.value = null;
        }
        if (audioContext.value) {
            audioContext.value.close();
            audioContext.value = null;
        }
        console.log("PCM 스트리밍 중지됨");
    }

    return { startStreaming, stopStreaming };
}
