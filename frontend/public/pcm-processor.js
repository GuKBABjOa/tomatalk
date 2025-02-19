class PCMProcessor extends AudioWorkletProcessor {
    process(inputs, outputs, parameters) {
        // 첫 번째 입력 채널을 가져옵니다.
        const input = inputs[0];
        if (input && input[0]) {
            const floatData = input[0]; // Float32Array 형태의 오디오 데이터 (한 채널)
            const pcmData = new Int16Array(floatData.length);
            let sum = 0;

            // 각 샘플을 Float32에서 Int16으로 변환하며, 절대값을 누적합니다.
            for (let i = 0; i < floatData.length; i++) {
                // 클리핑: -1과 1 사이의 값으로 제한
                let sample = Math.max(-1, Math.min(1, floatData[i]));
                // NaN 또는 무한대 값은 0으로 대체
                if (isNaN(sample) || !isFinite(sample)) sample = 0;
                // 변환: -1 -> -32768, 1 -> 32767
                pcmData[i] = Math.floor(sample * 32768);
                sum += Math.abs(pcmData[i]);
            }

            // 무음 감지: 모든 샘플의 절대값 합이 일정 임계값(여기서는 100)보다 크면 전송
            if (sum > 100) {
                // ArrayBuffer 형태로 메인 스크립트에 전송
                this.port.postMessage(pcmData.buffer);
            } else {
                // 무음인 경우 전송하지 않음 (디버깅 로그 출력)
                console.log('PCMProcessor: 무음으로 판단되어 전송 생략 (sum: ' + sum + ')');
            }

            // 로그: 변환된 데이터 길이 출력
            console.log('PCMProcessor: 처리된 샘플 수:', pcmData.length);
        }
        return true;
    }
}

registerProcessor('pcm-processor', PCMProcessor);
