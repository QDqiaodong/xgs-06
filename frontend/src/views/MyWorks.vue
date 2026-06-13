<template>
  <div class="my-works-page">
    <van-nav-bar title="我的作品" left-arrow @click-left="$router.back()" fixed />

    <div class="page-content">
      <WorkList v-if="userStore.userInfo" type="my" :user-id="userStore.userInfo.id" />
      <div v-else class="empty-state">
        <van-empty description="请先登录查看" />
        <van-button type="primary" @click="$router.push('/login')">去登录</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import WorkList from '@/components/WorkList.vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
</script>

<style scoped>
.my-works-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-content {
  padding-top: 46px;
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
}
</style>
