<template>
  <div class="debate-container">
    <h2 class="title">🔥많은 사람들이 방청 중인 토론이에요!</h2>
    <div class="cards-container">
      <div class="cards-wrapper">
        <div v-for="(debate, index) in sortedHotDebates" :key="debate.debateId" class="card-position"
          :style="getCardStyle(debate, index)" @mouseenter="hoveredId = debate.debateId" @mouseleave="hoveredId = null">
          <div :class="[
            'debate-card',
            hoveredId === debate.debateId ? 'hovered' : '',
          ]">
            <div :class="['rank-badge', index === 0 ? 'rank-first' : '']">
              {{ index + 1 }}
            </div>

            <div class="card-header">
              <div class="badges-container">
                <img v-if="index === 0" src="@/assets/hot-badge.svg" alt="HOT" class="hot-badge" />
              </div>
              <div class="viewers-count">
                <Users :size="16" />
                <span>{{ debate.spectatorsCount }}명</span>
              </div>
            </div>

            <div class="category">
              {{ getCategoryLabel(debate.category) }}
            </div>

            <h3 class="debate-title">
              {{ debate.subject }}
            </h3>

            <button :class="[
              'join-button',
              hoveredId === debate.debateId ? 'hovered' : '',
            ]" @click="handleSpectate(debate.debateId)">
              방청하기
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { Users, ArrowRight } from "lucide-vue-next";
import axios from "axios";
const backendUrl = import.meta.env.VITE_BACKEND_URL;

interface DebateInfoResponse {
  debateId: string;
  category: string;
  subject: string;
  status: string;
  startedAtHour: number;
  startedAtMinute: number;
  estimatedTimeMinute: number;
  spectatorsCount: number;
}

interface CursorResponse<T> {
  content: T[];
  size: number;
  first: boolean;
  last: boolean;
}

const hotDebates = ref<DebateInfoResponse[]>([]);
const hoveredId = ref<string | null>(null);
const router = useRouter();

const sortedHotDebates = computed(() => {
  return [...hotDebates.value].sort(
    (a, b) => b.spectatorsCount - a.spectatorsCount
  );
});

const getCardStyle = (debate: DebateInfoResponse, index: number) => {
  const positions = [
    { x: 0, z: 40, scale: 1.1 }, // center
    { x: -250, z: 20, scale: 0.95 }, // left
    { x: 250, z: 20, scale: 0.9 }, // right
  ];

  const isHovered = hoveredId.value === debate.debateId;
  const isAnyHovered = hoveredId.value !== null;
  const pos = positions[index];

  if (isHovered) {
    return {
      transform: `
        translate3d(${pos.x}px, -2rem, 100px)
        scale(${pos.scale * 1.05})
        rotate3d(0, 1, 0, 0deg)
      `,
      zIndex: 30,
    };
  }

  if (isAnyHovered) {
    return {
      transform: `
        translate3d(${pos.x}px, 0, ${pos.z - 20}px)
        scale(${pos.scale * 0.95})
        rotate3d(0, 1, 0, ${index === 1 ? 10 : index === 2 ? -10 : 0}deg)
      `,
      zIndex: index === 0 ? 20 : 10,
    };
  }

  return {
    transform: `
      translate3d(${pos.x}px, 0, ${pos.z}px)
      scale(${pos.scale})
      rotate3d(0, 1, 0, ${index === 0 ? 0 : index === 1 ? 10 : -10}deg)
    `,
    zIndex: index === 0 ? 20 : 10,
    opacity: 1,
  };
};

const fetchHotDebates = async () => {
  try {
    const response = await axios.get<CursorResponse<DebateInfoResponse>>(
      backendUrl + "/api/debates",
      {
        params: {
          cursor: 9007199254740991,
          size: 3,
          status: "IN_PROGRESS",
          sortBy: "POPULARITY",
        },
      }
    );

    hotDebates.value = response.data.content;
  } catch (error) {
    console.error("인기 토론을 불러오지 못했습니다:", error);
  }
};

const getCategoryLabel = (category: string) => {
  const categoryMap: Record<string, string> = {
    POLITICS: "정치/국제",
    ECONOMY: "경제/노동",
    ETHICS: "윤리/법",
    SCIENCE: "과학/기술",
    EDUCATION: "교육/사회",
  };
  return categoryMap[category] || category;
};

const handleSpectate = (debateId: string) => {
  router.push(`/debate/${debateId}`);
};

onMounted(() => {
  fetchHotDebates();
});
</script>

<style scoped>
.debate-container {
  padding: 2rem;
  position: relative;
  width: 100%;
  /* overflow: hidden; */
}

.title {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 2rem;
}

.cards-container {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  perspective: 2000px;
}

.cards-wrapper {
  position: relative;
  width: 100%;
  max-width: 72rem;
  height: 24rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-position {
  position: absolute;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.debate-card {
  position: relative;
  width: 20rem;
  padding: 1.5rem;
  border-radius: 0.75rem;
  background-color: rgba(255, 255, 255, 0.98);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  transition: all 0.5s ease;
}

.debate-card.hovered {
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 20px 25px -5px rgba(239, 68, 68, 0.3);
}

.rank-badge {
  position: absolute;
  top: -0.75rem;
  left: -0.75rem;
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  background-color: #5b5454;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  font-weight: bold;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transform: scale(1);
  transition: transform 0.3s ease;
}

.rank-badge.rank-first {
  background-color: #ef4444;
}

.badges-container {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.hot-badge {
  height: 24px;
  width: 60px;
  filter: drop-shadow(0 2px 4px rgba(255, 65, 108, 0.3));
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }

  50% {
    transform: scale(1.05);
  }

  100% {
    transform: scale(1);
  }
}

.debate-card:hover .rank-badge {
  transform: scale(1.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  margin-top: 0.5rem;
}

.viewers-count {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  color: #ef4444;
}

.viewers-count span {
  font-size: 0.875rem;
  font-weight: 500;
}

.category {
  margin-bottom: 0.75rem;
  font-size: 0.875rem;
  color: #4b5563;
}

.debate-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 1.5rem;
  min-height: 3.5rem;
}

.join-button {
  font-family: inherit;
  width: 100%;
  padding: 0.75rem 1rem;
  border-radius: 9999px;
  background-color: #f87171;
  color: white;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
}

.join-button.hovered {
  background-color: #ef4444;
  box-shadow: 0 4px 6px -1px rgba(239, 68, 68, 0.3);
  transform: scale(1.05);
}

.arrow-icon {
  transition: transform 0.3s ease;
}

.arrow-icon.shifted {
  transform: translateX(0.25rem);
}

.debate-title,
.category,
.live-badge,
.viewers-count {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>
