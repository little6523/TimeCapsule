// 로그아웃 버튼 클릭 시 실행되는 코드
const logoutButton = document.getElementById('logout-button');
if (logoutButton) {
    logoutButton.addEventListener('click', async (event) => {
        event.preventDefault(); // 기본 링크 동작 방지
        console.log("로그아웃 버튼 눌림!");

        // 로컬 스토리지에서 토큰 삭제
        localStorage.removeItem('jwtToken');

        // 서버에 로그아웃 요청 전송
        try {
            const accessToken = localStorage.getItem('jwtToken');
            const response = await fetch('/logout', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + accessToken
                }
            });

            if (response.ok) {
                alert('로그아웃 되었습니다.');
                window.location.href = '/login'; // 로그아웃 후 리다이렉트할 페이지 설정
            } else {
                alert('로그아웃에 실패했습니다.');
            }
        } catch (error) {
            console.error('로그아웃 중 오류 발생:', error);
            alert('로그아웃 중 오류가 발생했습니다.');
        }
    });
} else {
    console.error('로그아웃 버튼을 찾을 수 없습니다.');
}