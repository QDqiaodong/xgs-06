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

      <div class="craft-card" v-if="userStore.userInfo && craftProfile">
        <div class="craft-card-header">
          <van-icon name="certificate" size="20" color="#c08457" />
          <span class="craft-card-title">皮具创作名片</span>
        </div>

        <div class="craft-card-stats">
          <div class="stat-item">
            <span class="stat-value">{{ craftProfile.totalWorks || 0 }}</span>
            <span class="stat-label">累计作品</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ craftProfile.recentCompleted || 0 }}</span>
            <span class="stat-label">近30天完成</span>
          </div>
        </div>

        <div class="craft-card-section" v-if="craftProfile.topCategories && craftProfile.topCategories.length">
          <div class="section-label">擅长品类</div>
          <div class="tag-list">
            <span
              v-for="cat in craftProfile.topCategories"
              :key="'cat-' + cat.id"
              class="craft-tag category-tag"
            >
              {{ cat.name }}<small v-if="cat.count"> {{ cat.count }}件</small>
            </span>
          </div>
        </div>

        <div class="craft-card-section" v-if="craftProfile.topCraftTypes && craftProfile.topCraftTypes.length">
          <div class="section-label">常练工艺</div>
          <div class="tag-list">
            <span
              v-for="craft in craftProfile.topCraftTypes"
              :key="'craft-' + craft.id"
              class="craft-tag craft-type-tag"
            >
              {{ craft.name }}<small v-if="craft.count"> {{ craft.count }}件</small>
            </span>
          </div>
        </div>

        <div class="craft-card-section" v-if="craftProfile.commonMaterials && craftProfile.commonMaterials.length">
          <div class="section-label">常用皮料</div>
          <div class="tag-list">
            <span
              v-for="mat in craftProfile.commonMaterials"
              :key="'mat-' + mat"
              class="craft-tag material-tag"
            >
              {{ mat }}
            </span>
          </div>
        </div>

        <div class="craft-card-empty" v-if="isEmptyProfile">
          <van-icon name="records" size="32" color="#ddd" />
          <p>发布更多作品，自动生成你的创作名片</p>
        </div>
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
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getCraftProfile } from '@/api'
import BottomNav from '@/components/BottomNav.vue'

const router = useRouter()
const userStore = useUserStore()
const craftProfile = ref(null)

const isEmptyProfile = computed(() => {
  if (!craftProfile.value) return true
  const p = craftProfile.value
  return (
    (!p.topCategories || p.topCategories.length === 0) &&
    (!p.topCraftTypes || p.topCraftTypes.length === 0) &&
    (!p.commonMaterials || p.commonMaterials.length === 0) &&
    (!p.totalWorks || p.totalWorks === 0)
  )
})

const loadCraftProfile = async () => {
  if (userStore.userInfo?.id) {
    try {
      craftProfile.value = await getCraftProfile(userStore.userInfo.id)
    } catch {
      craftProfile.value = null
    }
  } else {
    craftProfile.value = null
  }
}

onMounted(loadCraftProfile)

watch(() => userStore.userInfo?.id, loadCraftProfile)
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

.craft-card {
  margin: 0 12px 12px;
  background: linear-gradient(135deg, #fdf8f0 0%, #f5e6d3 100%);
  border-radius: 14px;
  padding: 16px;
  border: 1px solid #e8d5bf;
}

.craft-card-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 14px;
}

.craft-card-title {
  font-size: 15px;
  font-weight: 600;
  color: #6b4c2a;
}

.craft-card-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
  margin-bottom: 14px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 10px;
  padding: 12px 0;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #8b6914;
}

.stat-label {
  font-size: 11px;
  color: #999;
}

.stat-divider {
  width: 1px;
  height: 30px;
  background: #ddd;
}

.craft-card-section {
  margin-bottom: 12px;
}

.craft-card-section:last-of-type {
  margin-bottom: 0;
}

.section-label {
  font-size: 12px;
  color: #8b7355;
  margin-bottom: 8px;
  font-weight: 500;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.craft-tag {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.craft-tag small {
  font-size: 10px;
  opacity: 0.7;
}

.category-tag {
  background: rgba(192, 132, 87, 0.15);
  color: #8b6914;
  border: 1px solid rgba(192, 132, 87, 0.3);
}

.craft-type-tag {
  background: rgba(76, 143, 102, 0.12);
  color: #3a7d4e;
  border: 1px solid rgba(76, 143, 102, 0.25);
}

.material-tag {
  background: rgba(100, 100, 160, 0.1);
  color: #555580;
  border: 1px solid rgba(100, 100, 160, 0.2);
}

.craft-card-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 0 8px;
}

.craft-card-empty p {
  font-size: 13px;
  color: #bbb;
  margin: 0;
}

.menu-group {
  margin-bottom: 12px;
}
</style>
