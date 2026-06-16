<template>
  <div class="work-card" @click="$router.push(`/work/${work.id}`)">
    <div class="card-image">
      <img :src="work.cover || (work.images && work.images[0])" :alt="work.title" loading="lazy" />
    </div>
    <div class="card-content">
      <h3 class="card-title">{{ work.title }}</h3>
      <div class="card-meta">
        <span class="category">{{ work.categoryName }}</span>
        <span class="stats">
          <van-icon name="eye-o" /> {{ work.viewCount || 0 }}
          <van-icon name="star-o" /> {{ work.favoriteCount || 0 }}
        </span>
      </div>
      <div class="card-materials" v-if="work.materialBrief">
        <span class="material-brief">{{ work.materialBrief }}</span>
      </div>
      <div class="card-materials" v-else-if="work.materialSummary">
        <MaterialSummary :material-summary="work.materialSummary" compact />
      </div>
      <div class="card-author">
        <van-image
          round
          size="24"
          :src="work.avatar || 'https://img.yzcdn.cn/vant/cat.jpeg'"
        />
        <span class="nickname">{{ work.nickname }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import MaterialSummary from './MaterialSummary.vue'

defineProps({
  work: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.work-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-image {
  width: 100%;
  aspect-ratio: 4/3;
  overflow: hidden;
  background: #f0f0f0;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-content {
  padding: 12px;
}

.card-title {
  font-size: 15px;
  font-weight: 500;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
  color: #999;
}

.card-materials {
  margin-bottom: 10px;
}

.material-brief {
  font-size: 12px;
  color: #666;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  background: #f8f4ef;
  padding: 6px 8px;
  border-radius: 6px;
  border-left: 3px solid #c08457;
}

.category {
  background: #f5e6d3;
  color: #8b6914;
  padding: 2px 8px;
  border-radius: 4px;
}

.stats {
  display: flex;
  gap: 8px;
  align-items: center;
}

.card-author {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nickname {
  font-size: 13px;
  color: #666;
}
</style>
