const createButton = document.getElementById('create-story-button')
createButton.addEventListener('click', function () {
    // Fetch API로 POST 요청 보내기
    fetch('/api/story', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'  // JSON 형식으로 전송
        },
        body: JSON.stringify({
            dialect: document.getElementById('dialect').value,
            speaker: document.getElementById('speaker').value
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json(); // 서버로부터 JSON 응답을 받음
        })
        .then(data => {
            console.log('Success:', data);
            // 성공 시 동작 처리 (예: 화면에 결과 표시)
        })
        .catch((error) => {
            console.error('Error:', error);
        });
})