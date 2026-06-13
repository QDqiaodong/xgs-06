<template>
  <div class="profile-page">
    <van-nav-bar title="个人中心" fixed />

    <div class="profile-content">
      <div class="user-header" v-if="userStore.userInfo">
        <van-image
          round
          size="72"
          :src="userStore.userInfo.avatar || 'https://img.yzcdn.cn/vant/cat.jpeg'"
        />
        <div class="user-info">
          <h2>{{ userStore.userInfo.nickname }}</h2>
          <p v-if="userStore.userInfo.bio">{{ userStore.userInfo.bio }}</p>
        </div>
        <van-button size="small" type="default" @click="userStore.logout(); router.push('/login')">
          退出
        </van-button>
      </div>

      <div class="user-header" v-else @click="router.push('/login')">
        <van-image round size="72" src="https://img.yzcdn.cn/vant/cat.jpeg" />
        <div class="user-info">
          <h2>点击登录</h2>
          <p>登录后可以发布作品、收藏内容</p>
        </div>
        <van-icon name="arrow" />
      </div>

      <van-cell-group inset class="menu-group">
        <van-cell title="我的作品" is-link @click="router.push('/my-works')">
          <template #icon><van-icon name="photo-o" size="20" /></template>
        </van-cell>
        <van-cell title="我的收藏" is-link @click="router.push('/favorites')">
          <template #icon><van-icon name="star-o" size="20" /></template>
        </van-cell>
      </van-cell-group>

      <van-cell-group inset class="menu-group">
        <van-cell title="关于平台">
          <template #icon><van-icon name="info-o" size="20" /></template>
        </van-cell>
        <van-cell title="意见反馈">
          <template #icon><van-icon name="comment-o" size="20" /></template>
        </van-cell>
      </van-cell-group>
    </div>

    <BottomNav />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import BottomNav from '@/components/BottomNav.vue'

const router = useRouter()
const userStore = useUserStore()
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  padding-bottom: 50px;
  background: #f5f5f5;
}

.profile-content {
  padding-top: 46px;
}

.user-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 16px;
  background: #fff;
  margin-bottom: 12px;
}

.user-info {
  flex: 1;
}

.user-info h2 {
  font-size: 18px;
  margin: 0 0 4px;
}

.user-info p {
  font-size: 13px;
  color: #999;
  margin: 0;
}

.menu-group {
  margin-bottom: 12px;
}
</style>
