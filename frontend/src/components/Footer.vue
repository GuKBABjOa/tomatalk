<template>
  <footer class="footer">
    <div class="footer-bottom">
      <div class="footer-links">
        <a href="#" class="footer-link">이용약관</a>
        <a href="#" class="footer-link">개인정보처리방침</a>
      </div>
    </div>
  </footer>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "Footer",
});
</script>

<style scoped>
.footer {
  background-color: #ffffff;
  padding: 48px;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.footer-links {
  display: flex;
  gap: 40px;
}

.footer-link {
  color: #949494;
  font-size: 16px;
  text-decoration: none;
}

.footer-link:hover {
  color: #000000;
}
</style>
