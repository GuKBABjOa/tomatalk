<template>
    <div class="root">
        <div class="container">
            <ul class="progressbar">
                <li v-for="i in length" :key="i" :class="{ active: i <= activeStep }">{{ i }}</li>
            </ul>
        </div>
    </div>
</template>

<script>
export default {
    props: ['length', 'activeStep'], // 현재 진행중인 Step
};
</script>

<style scoped>
/* ✅ 부모 컨테이너를 flex로 변경하여 정렬 */
.container {
    width: 100%;
    /* 너비 조정 */
    display: flex;
    justify-content: flex-end;
    /* 우측 정렬 */
    align-items: center;
    position: relative;
    padding-right: 20px;
    /* 우측 여백 추가 */
}

/* ✅ Progress Bar 스타일 */
.progressbar {
    display: flex;
    list-style: none;
    padding: 0;
    gap: 8px;
    /* 각 원 사이 간격 */
    position: relative;
}

/* ✅ 각 Step 점 (원 형태) */
.progressbar li {
    width: 35px;
    /* 크기 키움 */
    height: 35px;
    line-height: 35px;
    /* 텍스트 중앙 정렬 */
    text-align: center;
    border: 2px solid #bebebe;
    border-radius: 50%;
    background: white;
    color: #bebebe;
    font-weight: bold;
    font-size: 16px;
    /* 숫자 크기 조정 */
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
}

.progressbar li:not(:first-child):after {
    content: '';
    position: absolute;
    width: 100%;
    /* 선의 길이 */
    height: 3px;
    background: #e90101;
    top: 15px;
    left: -45px;
    z-index: -1;
}

/* ✅ 활성화된 원 */
.progressbar li.active {
    border-color: #ff6b6b !important;
    background: #ff6b6b !important;
    color: white !important;
}

/* ✅ 활성화된 선 */
.progressbar li.active:after {
    background: #ff6b6b !important;
}
</style>
