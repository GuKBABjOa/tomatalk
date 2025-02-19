<!-- FutureEducation.vue -->
<template>
  <div class="future-education-container">
    <!-- Hero Section -->
    <div class="hero-section">
      <h1 class="main-heading">
        미래 교육의 핵심,
        <span class="highlight">토론으로 시작하세요</span>
      </h1>
      <p class="sub-heading">
        세계는 이미 토론 중심 교육으로 변화하고 있습니다
      </p>
    </div>

    <!-- Global Cases Section -->
    <div class="cases-section">
      <!-- Harvard Case -->
      <div class="case-card">
        <div class="case-image">
          <div class="image-container harvard-image">
            <<img
              :src="harvardImage"
              alt="Harvard"
              style="width: 100%; height: 100%; object-fit: cover"
            />
          </div>
          <div class="overlay-gradient"></div>
        </div>
        <div class="case-content">
          <div class="case-badge">World Leading University</div>
          <h2>하버드 대학교</h2>
          <div class="stats-container">
            <div class="stat-item">
              <span class="stat-number">80</span>
              <span class="stat-label">%</span>
            </div>
            <div class="stat-description">
              <p>수업이 토론으로 진행됩니다</p>
              <p class="detail-text">
                비판적 사고와 문제해결 능력 개발에 초점을 맞춥니다
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- IB Case -->
      <div class="case-card">
        <div class="case-image">
          <div class="image-container ib-image">
            <img
              :src="Ib"
              alt="Harvard"
              style="width: 100%; height: 100%; object-fit: cover"
            />
          </div>
          <div class="overlay-gradient"></div>
        </div>
        <div class="case-content">
          <div class="case-badge">Global Education Programme</div>
          <h2>IB 교육과정</h2>
          <div class="stats-container">
            <div class="stat-icon">
              <div class="icon-circle">ToK</div>
            </div>
            <div class="stat-description">
              <p>Theory of Knowledge</p>
              <p class="detail-text">
                체계적인 토론과 비판적 사고 학습을 통해 미래 인재를 양성합니다
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Korea Transition Section -->
    <div class="korea-section">
      <h2 class="section-title">
        2025년, 한국 교육의 새로운 전환이 시작됩니다
      </h2>
      <div class="news-cards">
        <div class="news-card">
          <div class="news-content">
            <div class="news-marker"></div>
            <div class="news-text">
              <h3>교육부, 2025년까지 전국 100개교<br />IB 도입 추진</h3>
              <p class="news-date">연합뉴스 2024.01</p>
            </div>
            <span class="arrow-icon">→</span>
          </div>
        </div>
        <div class="news-card">
          <div class="news-content">
            <div class="news-marker"></div>
            <div class="news-text">
              <h3>경기도교육청, IB 교육과정<br />확대 시행</h3>
              <p class="news-date">한국경제 2024.02</p>
            </div>
            <span class="arrow-icon">→</span>
          </div>
        </div>
      </div>
    </div>

    <!-- CTA Section -->
    <div class="cta-section">
      <h2>토론 중심 교육으로의 전환, 함께 시작하세요</h2>
    </div>
  </div>
</template>

<script setup>
import harvardImage from "@/assets/Harvard.jpg";
import Ib from "@/assets/Ib-edu.jpg";
</script>

<style>
.future-education-container {
  font-family: "Pretendard", sans-serif;
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  background-color: #ffffff;
}

/* Hero Section */
.hero-section {
  text-align: center;
  margin-bottom: 60px;
}

.main-heading {
  font-size: 40px;
  font-weight: 700;
  color: #111827;
  line-height: 1.4;
  margin-bottom: 16px;
}

.highlight {
  color: #ff6b6b;
  display: inline-block;
}

.sub-heading {
  font-size: 20px;
  color: #4b5563;
  line-height: 1.6;
}

/* Cases Section */
.cases-section {
  display: grid;
  gap: 40px;
  margin-bottom: 60px;
}

.case-card {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  background: #ffffff;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.case-image {
  position: relative;
  height: 400px;
  overflow: hidden;
}

.image-container {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  transition: transform 0.3s ease-in-out;
}

.case-card:hover .image-container {
  transform: scale(1.05);
}

.harvard-image,
.ib-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.overlay-gradient {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 70%;
  background: linear-gradient(to bottom, transparent, rgba(0, 0, 0, 0.8));
}

.case-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 40px;
  color: #ffffff;
}

.case-badge {
  display: inline-block;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8px);
  border-radius: 20px;
  font-size: 14px;
  margin-bottom: 16px;
}

.case-content h2 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 24px;
}

.stats-container {
  display: flex;
  align-items: center;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: baseline;
}

.stat-number {
  font-size: 48px;
  font-weight: 700;
  line-height: 1;
}

.stat-label {
  font-size: 24px;
  margin-left: 4px;
}

.stat-icon {
  flex-shrink: 0;
}

.icon-circle {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8px);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 20px;
}

.stat-description {
  flex: 1;
}

.stat-description p {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
}

.detail-text {
  font-size: 16px;
  opacity: 0.9;
  font-weight: normal;
}

/* Korea Section */
.korea-section {
  background: #f8fafc;
  border-radius: 24px;
  padding: 40px;
  margin-bottom: 60px;
}

.section-title {
  text-align: center;
  font-size: 28px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 32px;
}

.news-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.news-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  transition: transform 0.2s ease;
}

.news-card:hover {
  transform: translateX(8px);
}

.news-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.news-marker {
  width: 8px;
  height: 8px;
  background: #ff6b6b;
  border-radius: 50%;
  flex-shrink: 0;
}

.news-text {
  flex: 1;
}

.news-text h3 {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  line-height: 1.5;
}

.news-date {
  font-size: 14px;
  color: #6b7280;
  margin-top: 8px;
}

.arrow-icon {
  color: #9ca3af;
  font-size: 24px;
}

/* CTA Section */
.cta-section {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8787 100%);
  border-radius: 24px;
  padding: 40px;
  text-align: center;
}

.cta-section h2 {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
}

/* Responsive Design */
@media (max-width: 768px) {
  .future-education-container {
    padding: 20px;
  }

  .main-heading {
    font-size: 32px;
  }

  .case-image {
    height: 300px;
  }

  .case-content {
    padding: 24px;
  }

  .case-content h2 {
    font-size: 28px;
  }

  .stat-number {
    font-size: 36px;
  }

  .stat-description p {
    font-size: 18px;
  }

  .news-cards {
    grid-template-columns: 1fr;
  }

  .korea-section {
    padding: 24px;
  }
}
</style>
