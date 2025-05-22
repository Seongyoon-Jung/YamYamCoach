<template>
  <div class="donut-chart">
    <svg width="100%" height="100%" viewBox="0 0 42 42">
      <!-- 배경 원 -->
      <circle
        class="donut-ring"
        cx="21"
        cy="21"
        r="15.91549430918954"
        fill="transparent"
        stroke="#e6e6e6"
        stroke-width="3"
      ></circle>

      <!-- 데이터 원 -->
      <circle
        class="donut-segment"
        cx="21"
        cy="21"
        r="15.91549430918954"
        fill="transparent"
        :stroke="color"
        stroke-width="3"
        :stroke-dasharray="`${percentage} ${100 - percentage}`"
        stroke-dashoffset="25"
      ></circle>

      <!-- 중앙 텍스트 -->
      <g class="donut-text">
        <text x="50%" y="50%" class="donut-value">
          {{ displayValue }} <tspan class="donut-suffix">{{ suffix }}</tspan>
        </text>
      </g>
    </svg>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  value: {
    type: Number,
    required: true
  },
  max: {
    type: Number,
    default: 100
  },
  suffix: {
    type: String,
    default: ''
  },
  color: {
    type: String,
    default: '#4caf50'
  },
  decimalPlaces: {
    type: Number,
    default: 0
  }
});

// 0-100 사이의 비율 계산
const percentage = computed(() => {
  const calc = (props.value / props.max) * 100;
  const result = Math.min(100, Math.max(0, calc)); // 0-100 사이로 제한
  console.log(`DonutChart: value=${props.value}, max=${props.max}, percentage=${result}%`);
  return result;
});

// 표시할 값 계산
const displayValue = computed(() => {
  if (props.decimalPlaces > 0) {
    return props.value.toFixed(props.decimalPlaces);
  }
  return Math.round(props.value);
});
</script>

<style scoped>
.donut-chart {
  width: 100%;
  height: 100%;
  position: relative;
}

.donut-segment {
  transition: stroke-dasharray 0.5s ease-in-out;
}

.donut-text {
  font-family: Arial, sans-serif;
  text-anchor: middle;
}

.donut-value {
  font-size: 0.5em;
  font-weight: 800;
  fill: #333;
  dominant-baseline: central;
}

.donut-suffix {
  font-size: 0.5em;
  font-weight: 700;
  fill: #666;
}
</style> 