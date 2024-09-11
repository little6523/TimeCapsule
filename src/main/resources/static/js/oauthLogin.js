const isLoggedIn = sessionStorage.getItem('jwtToken');

if(!isLoggedIn) {
    const urlParams = new URLSearchParams(window.location.search);
    const oneTimeCode = urlParams.get('code');
    const error = urlParams.get('error');
    const errorMessage = urlParams.get('message');

    if (error === 'email_exists') {
        alert(errorMessage);
        window.location.href = '/login';
    } else if (oneTimeCode) {
        // 서버에 일회용 코드를 보내고 액세스 토큰 요청
        fetch('/oauth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ oneTimeCode: oneTimeCode }),
        })
            .then(response => response.json())
            .then(data => {
                if (data.accessToken) {
                    // 액세스 토큰을 로컬 스토리지에 저장
                    sessionStorage.setItem('jwtToken', data.accessToken);

                    // 메인 페이지로 리다이렉트
                    window.location.href = '/main';
                } else {
                    console.error('Failed to retrieve access token');
                    alert('로그인 요청 중 오류가 발생했습니다. 다시 시도해 주세요.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('로그인 요청 중 오류가 발생했습니다. 다시 시도해 주세요.');
            });
    }
} else {
    //이미 로그인 상태인 경우
    window.location.href = '/main';
}
