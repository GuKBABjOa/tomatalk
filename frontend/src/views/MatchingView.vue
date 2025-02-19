<template>
    <div class="match-container mx-auto justify-center">
        <nav class="mb-2.5 flex relative w-full h-24">
            <button class="before absolute" @click="resetMatching"></button>
            <div class="flex justify-center w-full text-center">
                <p v-if="!matching" class="content-center text-red-500 text-2xl">{{ time }}초 후 시작됩니다.</p>
            </div>
        </nav>
        <div v-if="matching" class="loading">
            <p>상대를 찾고 있어요</p>
            <p>잠시만 기다려주세요</p>
            <img class="mx-auto" src="/img/Loader.png" alt="Loading" />
        </div>
        <div v-else class="match-complete">
            <p>상대를 찾았어요</p>
            <p>내 순서를 확인해보세요</p>
            <img class="mx-auto mb-3.5" src="/img/checkcircle.png" alt="check" />
            <!-- 선/후공 알려주는 영역 -->
            <div class="mb-3.5">
                <p class="mb-1 text-2xl">내 순서는?</p>
                <div class="bg-white w-5/6 mx-auto py-2 box">
                    <p class="mb-0 whitebox_text">{{ turn }}</p>
                </div>
            </div>
            <!-- 선공/후공 설명 토글 -->
            <div>
                <p class="mb-1 text-2xl">선공/후공이 무엇인가요?</p>
                <div @click="toggleGameMethod" class="bg-white w-5/6 mx-auto py-2 cursor-pointer box">
                    <p class="whitebox_text">게임 방법 보기 <span class="mx-0 my-0"
                            :class="['arrow', showGameMethod ? 'arrow-up' : 'arrow-down']"></span></p>
                    <transition name="fade">
                        <div v-if="showGameMethod" class="game-method">
                            <p class="mt-2.5 px-3 ">총 3라운드로 진행합니다.<br><br>
                                한 라운드 당<br>선공 30초 → 후공 30초입니다.<br><br>
                                발언 시작 전 준비시간 5초가 주어집니다.
                            </p>
                        </div>
                    </transition>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const backendUrl = import.meta.env.VITE_BACKEND_URL;

const matching = ref(true);
const showGameMethod = ref(false);
const router = useRouter();
const turn = ref('선공');
const time = ref(5);

function toggleGameMethod() {
    showGameMethod.value = !showGameMethod.value;
}

const matchingData = async () => {
    try {
        const response = await axios.get(`${backendUrl}/matching`);
        console.log(response.data);
        turn.value = response.data.turn // 선 후공 정보 변경
        // 5초 대기 후 페이지 이동
        setTimeout(() => {
            router.push({ name: 'DiscussionPage' });
        }, 5000);
    } catch (error) {
        console.error(error);
    }
};

matchingData();

// test용 연결되고 5초동안 로딩 화면
onMounted(() => {
    setTimeout(() => {
        matching.value = false;
    }, 5000);
});

function resetMatching() {
    matching.value = true;
    setTimeout(() => {
        matching.value = false;
    }, 5000);
}

// 매칭 상태 감시
watch(matching, (newValue) => {
    if (!newValue) {
        startCountdown();
    }
});

// 5초 카운트다운
function startCountdown() {
    let count = 5;
    const interval = setInterval(() => {
        count--;
        time.value = count;
        if (count === 0) {
            clearInterval(interval);
        }
    }, 1000);
}
</script>

<style scoped>
.match-container {
    /* 화면 너비 관련해서는 rem으로 추후 통일 예정 */
    min-width: 412px;
    max-width: 480px;
    height: 100vh;
    font-size: 18px;
    background-color: #f3f3f3;
    /* display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center; */
}

.loading>p,
.match-complete>p {
    /* margin-bottom: 20px; */
    text-align: center;
    font-size: 2rem;
    color: #616161;
    margin-bottom: 2.5rem;
}

.loading p,
.match-complete p {
    text-align: center;
    color: #616161;
}

.loading img {
    width: 3rem;
    height: 3rem;
    animation: spin 2s linear infinite;
}

.match-complete img {
    width: 3rem;
    height: 3rem;
}

@keyframes spin {
    100% {
        transform: rotate(360deg);
    }
}

.fade-enter-active,
.fade-leave-active {
    transition: max-height 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
    max-height: 0px;
}

.fade-enter-to,
.fade-leave-from {
    max-height: 200px;
    /* 적절한 max-height 값 설정 */
}

.game-method {
    overflow: hidden;
}

.game-method p {
    text-align: left;
    font-size: 1.25rem;
}

.box {
    border-radius: 6px;
    margin-bottom: 2rem;
}

.before {
    border: solid black;
    border-width: 0 0 0.125rem 0.125rem;
    padding: 0.5rem;
    transform: rotate(45deg);
    /* margin-top: 2.5rem;
    margin-left: 2rem; */
    top: 44%;
    left: 10%;
}

.arrow {
    display: inline-block;
    border: solid black;
    border-width: 0 0.125rem 0.125rem 0;
    padding: 0.5rem;
    transform: rotate(45deg);
    /* 기본은 아래 화살표 */
    transition: transform 0.3s ease;
}

.arrow-down {
    transform: rotate(45deg);
    -webkit-transform: rotate(45deg);
    margin-bottom: 0.6rem;
}

.arrow-up {
    transform: rotate(-135deg);
    /* 위쪽 화살표 */
    /* margin-bottom: 0.1rem; */
}

.whitebox_text {
    font-size: 2rem;
}
</style>
