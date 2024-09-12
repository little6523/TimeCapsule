// 현재 페이지 번호 유지를 위한 함수
// 현재 URL의 페이지 번호를 sessionStorage에 저장
window.onload = function() {
    const params = new URLSearchParams(window.location.search);
    const pageNumber = params.get('page') || 1;  // 페이지 번호가 없으면 기본값 1
    sessionStorage.setItem('currentPage', pageNumber);
}