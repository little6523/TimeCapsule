//storyId: html 인라인 선언
// 삭제 모달창
document.getElementById("deleteBtn").addEventListener("click", function () {

    if (confirm("정말로 삭제하시겠습니까?")) {
        fetch('/stories/' + storyId, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    let modal = document.getElementById("deleteModal");
                    modal.style.display = "flex";
                } else {
                    alert("삭제에 실패했습니다. 다시 시도해주세요.");
                }
            })
            .catch(error => {
                console.error('Error: ', error);
                alert("삭제에 실패했습니다. 다시 시도해주세요.");
            });
    }
});

document.getElementById("closeModal").addEventListener("click", function () {
    let modal = document.getElementById("deleteModal");
    modal.style.display = "none";
    window.location.href = "/stories";
});