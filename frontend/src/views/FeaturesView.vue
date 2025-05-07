<template>
  <main class="py-5">
    <div class="container">
      <!-- Page Header -->
      <section class="text-center mb-5">
        <h1 class="display-5 fw-bold">냠냠코치 기능 소개</h1>
        <p class="lead">다양한 데이터 구조와 알고리즘을 활용한 기본 및 추가 기능을 확인하세요.</p>
      </section>

      <!-- 기본 기능 (Data Structures & Algorithms) -->
      <section class="mb-5">
        <h2 class="h4 fw-bold mb-3">기본 기능</h2>
        <div class="table-responsive">
          <table class="table table-striped align-middle">
            <thead class="table-dark">
              <tr>
                <th scope="col">번호</th>
                <th scope="col">데이터 / 기능</th>
                <th scope="col">자료구조</th>
                <th scope="col">알고리즘</th>
                <th scope="col">비고</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in basicFeatures" :key="item.id">
                <th scope="row">{{ item.id }}</th>
                <td>{{ item.name }}</td>
                <td>{{ item.dataStructure }}</td>
                <td>{{ item.algorithm }}</td>
                <td>{{ item.note }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- 추가 기능 -->
      <section>
        <h2 class="h4 fw-bold mb-3">추가 기능</h2>
        <div class="row g-4">
          <div class="col-md-6" v-for="feature in additionalFeatures" :key="feature.id">
            <div class="card h-100 shadow-sm border-0">
              <div class="card-body">
                <h5 class="card-title">{{ feature.title }}</h5>
                <p class="card-text">{{ feature.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </main>
</template>

<script>
export default {
  name: 'FeaturesView',
  data() {
    return {
      basicFeatures: [
        { id: 'DS-01', name: '음식 초성 검색 + 영어 오타 검색', dataStructure: 'Trie, BK-Tree, Map', algorithm: '자체 알고리즘 구현', note: '' },
        { id: 'DS-02', name: 'MBTI + 설문 기반 태그 저장', dataStructure: 'Map, Set', algorithm: '키-값 기반 설문 태그 저장', note: '' },
        { id: 'DS-03', name: '질환, 알러지, 식습관 정보 저장', dataStructure: 'Map, List', algorithm: '유저-질환 매핑', note: '' },
        { id: 'DS-04', name: '건강 QnA 기능', dataStructure: 'PriorityQueue, Map', algorithm: '질문 우선순위 관리', note: '전문가 배정' },
        { id: 'DS-05', name: '캘린더 + 음식 사진 업로드', dataStructure: 'Map<LocalDate, Image>', algorithm: '날짜 기반 이미지 매핑', note: 'DB 이미지 저장 필요' },
        { id: 'DS-06', name: '커뮤니티 게시판', dataStructure: 'List, Map', algorithm: '게시글 ↔ 댓글 매핑', note: '' },
        { id: 'DS-07', name: '레시피 등록 및 거래', dataStructure: 'Map, List', algorithm: '포인트 거래 기록', note: '' },
        { id: 'Algo-01', name: '콘텐츠 기반 추천 (CBF)', dataStructure: 'double[], Vector', algorithm: 'Cosine Similarity', note: '정규화 필요' },
        { id: 'Algo-02', name: '목표 기반 음식+운동 추천', dataStructure: '우선순위 큐', algorithm: '조건 필터링 + 정렬', note: '' },
        { id: 'Algo-03', name: '영양제 보충 추천', dataStructure: 'Map, List', algorithm: '결핍 영양소 분석', note: '' },
        { id: 'Algo-05', name: '질병 맞춤 식단 추천', dataStructure: 'Map', algorithm: '질병-음식 매핑', note: '' },
        { id: 'Algo-06', name: '식단 잔디 기능', dataStructure: '2D Array, Graph', algorithm: '날짜별 시각화', note: '' }
      ],
      additionalFeatures: [
        { id: 'F-01', title: '개인 건강 목표 설정', description: '사용자가 목표를 설정하고 진행 상황을 추적할 수 있습니다.' },
        { id: 'F-02', title: '주변 식당 추천', description: '부족한 영양소에 맞춰 주변 식당을 추천합니다.' },
        { id: 'F-03', title: '식판 이미지 분석', description: '업로드된 사진을 분석하여 영양소를 자동으로 파악합니다.' },
        { id: 'F-04', title: '건강 통계 반영', description: '국민 건강 통계와 연령별 질병 특이 사항을 설문에 반영합니다.' },
        { id: 'F-05', title: '커뮤니티 & QnA', description: '전문가 QnA와 사용자 칼럼을 통해 정보를 공유합니다.' },
        { id: 'F-06', title: '레시피 마켓플레이스', description: '유저 간 레시피 거래 및 포인트 시스템을 지원합니다.' }
      ]
    }
  }
}
</script>

<style scoped>
section {
  padding-top: 2rem;
}
.table {
  font-size: 0.9rem;
}
.card {
  border-radius: 0.75rem;
}
.card-title {
  font-size: 1.25rem;
  margin-bottom: 0.5rem;
}
</style>
