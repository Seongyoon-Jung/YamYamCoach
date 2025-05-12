<template>
    <div class="col-lg-8 mb-4">
        <div class="card shadow-sm h-100">
            <ul class="nav nav-tabs card-header-tabs px-3">
                <li class="nav-item">
                <a class="nav-link active" href="#">칼로리</a>
                </li>
                <li class="nav-item">
                <a class="nav-link" href="#">탄수화물</a>
                </li>
                <li class="nav-item">
                <a class="nav-link" href="#">단백질</a>
                </li>
            </ul>
            <div class="card-body">
                <div class="chart-placeholder d-flex align-items-center justify-content-center" style="height: 300px;">
                <!-- 여기에 차트 컴포넌트 삽입 -->
                Line Chart Here
                </div>
            </div>
        </div>
    </div>
</template>