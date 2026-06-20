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
          <div class="stat-item" @click="activeStatTab = 'status'">
            <span class="stat-value">{{ craftProfile.totalWorks || 0 }}</span>
            <span class="stat-label">累计作品</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ craftProfile.recentCompleted || 0 }}</span>
            <span class="stat-label">近30天完成</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item" @click="activeStatTab = 'category'">
            <span class="stat-value">{{ craftProfile.categoryStats?.length || 0 }}</span>
            <span class="stat-label">覆盖品类</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item" @click="activeStatTab = 'craft'">
            <span class="stat-value">{{ craftProfile.craftTypeStats?.length || 0 }}</span>
            <span class="stat-label">涉及工艺</span>
          </div>
        </div>

        <div class="stats-tabs">
          <div
            class="stats-tab"
            :class="{ active: activeStatTab === 'status' }"
            @click="activeStatTab = 'status'"
          >
            <van-icon name="flag-o" size="14" />
            <span>按状态</span>
          </div>
          <div
            class="stats-tab"
            :class="{ active: activeStatTab === 'category' }"
            @click="activeStatTab = 'category'"
          >
            <van-icon name="cluster-o" size="14" />
            <span>按品类</span>
          </div>
          <div
            class="stats-tab"
            :class="{ active: activeStatTab === 'craft' }"
            @click="activeStatTab = 'craft'"
          >
            <van-icon name="wap-nav" size="14" />
            <span>按工艺</span>
          </div>
        </div>

        <div class="stats-panel">
          <div v-show="activeStatTab === 'status'" class="status-panel">
            <div v-if="hasStatusStats" class="status-list">
              <div
                v-for="item in displayStatusStats"
                :key="item.status"
                class="status-bar-item"
              >
                <div class="bar-label-row">
                  <span class="bar-label" :class="'bar-label--' + item.status">
                    <span class="bar-icon">{{ getWorkStatusIcon(item.status) }}</span>
                    {{ item.statusName }}
                  </span>
                  <span class="bar-count">{{ item.count }}件</span>
                </div>
                <div class="bar-track">
                  <div
                    class="bar-fill"
                    :class="'bar-fill--' + item.status"
                    :style="{ width: getBarPercent(item.count) + '%' }"
                  ></div>
                </div>
              </div>
            </div>
            <div v-else class="stats-empty">
              <van-icon name="records" size="28" color="#ddd" />
              <span>暂无状态分布数据</span>
            </div>
          </div>

          <div v-show="activeStatTab === 'category'" class="category-panel">
            <div v-if="hasCategoryStats" class="category-list">
              <div
                v-for="item in displayCategoryStats"
                :key="item.id"
                class="category-bar-item"
              >
                <div class="bar-label-row">
                  <span class="bar-label category-bar-label">
                    <span class="bar-dot"></span>
                    {{ item.name }}
                  </span>
                  <span class="bar-count">{{ item.count }}件</span>
                </div>
                <div class="bar-track">
                  <div
                    class="bar-fill bar-fill--category"
                    :style="{ width: getBarPercent(item.count) + '%' }"
                  ></div>
                </div>
              </div>
            </div>
            <div v-else class="stats-empty">
              <van-icon name="records" size="28" color="#ddd" />
              <span>暂无品类分布数据</span>
            </div>
          </div>

          <div v-show="activeStatTab === 'craft'" class="craft-panel">
            <div v-if="hasCraftStats" class="craft-list">
              <div
                v-for="item in displayCraftStats"
                :key="item.id"
                class="craft-bar-item"
              >
                <div class="bar-label-row">
                  <span class="bar-label craft-bar-label" :class="getCraftClass(item.name)">
                    <span class="craft-bar-icon">{{ getCraftInfoByName(item.name).icon }}</span>
                    {{ item.name }}
                  </span>
                  <span class="bar-count">{{ item.count }}件</span>
                </div>
                <div class="bar-track">
                  <div
                    class="bar-fill bar-fill--craft"
                    :style="{ width: getBarPercent(item.count) + '%' }"
                  ></div>
                </div>
              </div>
            </div>
            <div v-else class="stats-empty">
              <van-icon name="records" size="28" color="#ddd" />
              <span>暂无工艺分布数据</span>
            </div>
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
        <van-cell title="我的复盘记录" is-link @click="router.push('/my-retrospectives')">
          <template #icon><van-icon name="records-o" size="20" /></template>
          <template #value>
            <span class="retro-count" v-if="retroCount > 0">{{ retroCount }} 篇</span>
          </template>
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
import { getCraftProfile, getMyRetrospectives } from '@/api'
import { getCraftInfoByName, getCraftClass, getWorkStatusInfo } from '@/utils/craftConfig'
import BottomNav from '@/components/BottomNav.vue'

const router = useRouter()
const userStore = useUserStore()
const craftProfile = ref(null)
const retroCount = ref(0)
const activeStatTab = ref('status')

const isEmptyProfile = computed(() => {
  if (!craftProfile.value) return true
  const p = craftProfile.value
  return (
    (!p.categoryStats || p.categoryStats.length === 0) &&
    (!p.craftTypeStats || p.craftTypeStats.length === 0) &&
    (!p.workStatusStats || p.workStatusStats.length === 0) &&
    (!p.commonMaterials || p.commonMaterials.length === 0) &&
    (!p.totalWorks || p.totalWorks === 0)
  )
})

const maxStatCount = computed(() => {
  const counts = []
  if (craftProfile.value?.workStatusStats?.length) {
    counts.push(...craftProfile.value.workStatusStats.map(s => s.count))
  }
  if (craftProfile.value?.categoryStats?.length) {
    counts.push(...craftProfile.value.categoryStats.map(s => s.count))
  }
  if (craftProfile.value?.craftTypeStats?.length) {
    counts.push(...craftProfile.value.craftTypeStats.map(s => s.count))
  }
  return counts.length ? Math.max(...counts) : 0
})

const hasStatusStats = computed(() => {
  return craftProfile.value?.workStatusStats?.length > 0
})

const hasCategoryStats = computed(() => {
  return craftProfile.value?.categoryStats?.length > 0
})

const hasCraftStats = computed(() => {
  return craftProfile.value?.craftTypeStats?.length > 0
})

const displayStatusStats = computed(() => {
  if (!craftProfile.value?.workStatusStats) return []
  const statusOrder = ['finished', 'practice', 'semi_finished', 'repair']
  const sorted = [...craftProfile.value.workStatusStats].sort((a, b) => {
    const ia = statusOrder.indexOf(a.status)
    const ib = statusOrder.indexOf(b.status)
    if (ia === -1 && ib === -1) return b.count - a.count
    if (ia === -1) return 1
    if (ib === -1) return -1
    return ia - ib
  })
  return sorted
})

const displayCategoryStats = computed(() => {
  if (!craftProfile.value?.categoryStats) return []
  return [...craftProfile.value.categoryStats].sort((a, b) => b.count - a.count)
})

const displayCraftStats = computed(() => {
  if (!craftProfile.value?.craftTypeStats) return []
  return [...craftProfile.value.craftTypeStats].sort((a, b) => b.count - a.count)
})

const getBarPercent = (count) => {
  if (!maxStatCount.value) return 0
  return Math.max(5, (count / maxStatCount.value) * 100)
}

const getWorkStatusIcon = (status) => {
  const info = getWorkStatusInfo(status)
  return info?.icon || '📄'
}

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

const loadRetroCount = async () => {
  if (!userStore.userInfo?.id) {
    retroCount.value = 0
    return
  }
  try {
    const res = await getMyRetrospectives({ page: 1, size: 1 })
    retroCount.value = res?.total || 0
  } catch {
    retroCount.value = 0
  }
}

onMounted(() => {
  loadCraftProfile()
  loadRetroCount()
})

watch(() => userStore.userInfo?.id, () => {
  loadCraftProfile()
  loadRetroCount()
})
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
  cursor: pointer;
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

.stats-tabs {
  display: flex;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 10px;
  padding: 4px;
  margin-bottom: 14px;
  gap: 4px;
}

.stats-tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 8px 4px;
  border-radius: 8px;
  font-size: 13px;
  color: #8b7355;
  transition: all 0.2s ease;
  cursor: pointer;
}

.stats-tab.active {
  background: linear-gradient(135deg, #8b5a2b, #a0522d);
  color: #fff;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(139, 90, 43, 0.25);
}

.stats-panel {
  min-height: 120px;
}

.stats-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 24px 0;
  font-size: 13px;
  color: #bbb;
}

.status-list,
.category-list,
.craft-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.bar-label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
}

.bar-label {
  font-size: 13px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.bar-icon {
  font-size: 14px;
}

.bar-count {
  font-size: 12px;
  color: #888;
  font-weight: 600;
}

.bar-track {
  height: 8px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.4s ease;
}

.bar-fill--finished {
  background: linear-gradient(90deg, #52c41a, #73d13d);
}

.bar-label--finished {
  color: #389e0d;
}

.bar-fill--practice {
  background: linear-gradient(90deg, #8c8c8c, #a6a6a6);
}

.bar-label--practice {
  color: #595959;
}

.bar-fill--repair {
  background: linear-gradient(90deg, #fa8c16, #ffa940);
}

.bar-label--repair {
  color: #d46b08;
}

.bar-fill--semi_finished {
  background: linear-gradient(90deg, #1890ff, #40a9ff);
}

.bar-label--semi_finished {
  color: #096dd9;
}

.bar-fill--category {
  background: linear-gradient(90deg, #c08457, #d4a373);
}

.category-bar-label {
  color: #8b6914;
}

.bar-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #c08457;
}

.bar-fill--craft {
  background: linear-gradient(90deg, #722ed1, #9254de);
}

.craft-bar-label {
  padding: 2px 8px;
  border-radius: 10px;
  border: 1px solid transparent;
  background: rgba(114, 46, 209, 0.1);
  color: #531dab;
}

.craft-bar-icon {
  font-size: 13px;
}

.craft-card-section {
  margin-top: 14px;
  margin-bottom: 12px;
  padding-top: 14px;
  border-top: 1px dashed rgba(192, 132, 87, 0.3);
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
  border: 1px solid transparent;
}

.craft-tag small {
  font-size: 10px;
  opacity: 0.7;
}

.craft-badge-icon {
  font-size: 13px;
}

.category-tag {
  background: rgba(192, 132, 87, 0.15);
  color: #8b6914;
  border: 1px solid rgba(192, 132, 87, 0.3);
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

.retro-count {
  font-size: 12px;
  color: #c08457;
}
</style>
