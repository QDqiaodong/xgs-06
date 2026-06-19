<template>
  <div class="work-card" :class="density" @click="$router.push(`/work/${work.id}`)">
    <template v-if="density === 'compact'">
      <div class="compact-image">
        <img :src="work.cover || (work.images && work.images[0])" :alt="work.title" loading="lazy" />
        <div class="offline-badge" v-if="work.status === 0">已下架</div>
      </div>
      <div class="compact-content">
        <h3 class="compact-title">{{ work.title }}</h3>
        <div class="compact-tags">
          <van-tag v-if="work.categoryName" type="primary" size="medium" plain>
            {{ work.categoryName }}
          </van-tag>
          <span v-if="work.craftTypeName" class="craft-badge-tag" :class="getCraftClass(work.craftTypeName)">
            {{ getCraftInfoByName(work.craftTypeName).icon }} {{ work.craftTypeName }}
          </span>
          <span
            v-if="work.difficulty && getDifficultyInfo(work.difficulty)"
            class="difficulty-badge-tag"
            :class="getDifficultyClass(work.difficulty)"
          >
            {{ getDifficultyInfo(work.difficulty).icon }} {{ getDifficultyInfo(work.difficulty).name }}
          </span>
        </div>
        <div class="compact-stats">
          <van-icon name="eye-o" /> {{ work.viewCount || 0 }}
          <van-icon name="star-o" /> {{ work.favoriteCount || 0 }}
        </div>
      </div>
    </template>

    <template v-else>
      <div class="card-image">
        <img :src="work.cover || (work.images && work.images[0])" :alt="work.title" loading="lazy" />
        <div class="offline-badge" v-if="work.status === 0">已下架</div>
      </div>
      <div class="card-content">
        <h3 class="card-title">{{ work.title }}</h3>
        <div class="card-meta">
          <div class="card-tags">
            <span class="category">{{ work.categoryName }}</span>
            <span v-if="work.craftTypeName" class="craft-badge-tag" :class="getCraftClass(work.craftTypeName)">
              {{ getCraftInfoByName(work.craftTypeName).icon }} {{ work.craftTypeName }}
            </span>
            <span
              v-if="work.difficulty && getDifficultyInfo(work.difficulty)"
              class="difficulty-badge-tag"
              :class="getDifficultyClass(work.difficulty)"
            >
              {{ getDifficultyInfo(work.difficulty).icon }} {{ getDifficultyInfo(work.difficulty).name }}
            </span>
          </div>
          <span class="stats">
            <van-icon name="eye-o" /> {{ work.viewCount || 0 }}
            <van-icon name="star-o" /> {{ work.favoriteCount || 0 }}
          </span>
        </div>
        <div class="card-materials" v-if="work.materialBrief">
          <span class="material-brief">{{ work.materialBrief }}</span>
        </div>
        <div class="card-materials" v-else-if="work.materialSummary || work.materials">
          <MaterialSummary :material-summary="work.materialSummary" :materials-text="work.materials" compact />
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
    </template>
  </div>
</template>

<script setup>
import MaterialSummary from './MaterialSummary.vue'
import { getCraftInfoByName, getCraftClass, getDifficultyInfo, getDifficultyClass } from '@/utils/craftConfig'

defineProps({
  work: {
    type: Object,
    required: true
  },
  density: {
    type: String,
    default: 'standard'
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

.work-card.compact {
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.compact-image {
  width: 100%;
  aspect-ratio: 1/1;
  overflow: hidden;
  background: #f0f0f0;
  position: relative;
}

.compact-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.compact-content {
  padding: 8px 10px;
}

.compact-title {
  font-size: 13px;
  font-weight: 500;
  margin: 0 0 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #333;
}

.compact-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 6px;
}

.compact-stats {
  display: flex;
  gap: 8px;
  align-items: center;
  font-size: 11px;
  color: #999;
}

.card-image {
  width: 100%;
  aspect-ratio: 4/3;
  overflow: hidden;
  background: #f0f0f0;
  position: relative;
}

.offline-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

.compact .offline-badge {
  font-size: 10px;
  padding: 1px 6px;
  top: 6px;
  left: 6px;
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

.card-tags {
  display: flex;
  gap: 6px;
  align-items: center;
  flex-wrap: wrap;
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

.craft-badge-tag {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  padding: 1px 6px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
  border: 1px solid transparent;
}

.difficulty-badge-tag {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  padding: 1px 7px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
  border: 1px solid transparent;
}

.difficulty-badge-tag.difficulty-badge--beginner {
  background: #f6ffed;
  color: #389e0d;
  border-color: #b7eb8f;
}

.difficulty-badge-tag.difficulty-badge--intermediate {
  background: #fff7e6;
  color: #d46b08;
  border-color: #ffd591;
}

.difficulty-badge-tag.difficulty-badge--advanced {
  background: #fff1f0;
  color: #cf1322;
  border-color: #ffa39e;
}
</style>
