<script setup lang="ts">
import SiteFooter from '@/components/common/SiteFooter.vue'
import SiteHeader from '@/components/common/SiteHeader.vue'
</script>

<template>
  <div class="portal-shell">
    <a class="skip-link" href="#main-content">跳到主要内容</a>
    <SiteHeader />
    <main id="main-content" tabindex="-1"><RouterView /></main>
    <SiteFooter />
  </div>
</template>

<style scoped>
.portal-shell {
  min-height: 100vh;
  background: var(--color-canvas);
}
main {
  min-height: 60vh;
}
</style>
