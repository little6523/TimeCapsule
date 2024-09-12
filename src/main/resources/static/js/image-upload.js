const imageUploadInput = document.getElementById('imageUpload');
const uploadBtn = document.getElementById('uploadBtn');

// 기존 이미지, 콘텐츠 로드
const content = contentEditableDiv.innerText;
const images = [];  // 업로드된 파일들을 저장할 배열

// 이미지 업로드 버튼 클릭 시 파일 선택창 열기
uploadBtn.addEventListener('click', function () {
    imageUploadInput.click();
})

imageUploadInput.addEventListener('change', function () {
    const files = Array.from(imageUploadInput.files);

    // 선택한 모든 파일을 images 배열에 추가
    files.forEach(file => {
        images.push(file);
    });

    // 이미지를 화면에 표시 (선택적 기능)
    files.forEach(file => {
        const reader = new FileReader();
        reader.onload = e => insertImage(e.target.result);  // 이미지를 화면에 표시
        reader.readAsDataURL(file);
    });

    imageUploadInput.value = ''; // 파일 선택창 초기화
});

let redirectUrl = '';  // 리다이렉트할 URL을 저장할 변수

saveButton.addEventListener('click', function () {
    let formData = new FormData(document.getElementById('createForm'));  // 폼 데이터를 가져옴

    // 선택한 파일들을 FormData에 추가
    images.forEach((file, index) => {
        formData.append('images', file);  // "images"는 컨트롤러에서 받을 파라미터 이름
    });

    formData.set('content', document.getElementById('content').innerText);

    // 체크박스 값 처리
    const isShared = document.getElementById('communityToggle').checked;
    formData.set('isShared', isShared); // 체크박스의 실제 값으로 설정

    // 서버에 폼 데이터를 전송
    fetch('/stories', {
        method: "POST",
        body: formData
    }).then(response =>
        redirectUrl = response.url)
        .then(data => {
            if (data) {
                storyModalContent.innerText = '생성한 스토리가 저장되었습니다.';
                storyModal.style.display = 'block';
            } else {
                console.error('스토리 저장에 실패했습니다.');
            }
        }).catch(error => console.error('Error:', error));
});

// 모달의 확인 버튼 클릭 시 리다이렉트 처리
confirmButton.addEventListener('click', function () {
    if (redirectUrl) {
        window.location.href = redirectUrl;
    }

    else if (storyModalContent.innerText === '스토리 생성을 취소하였습니다.') {
        window.location.href = '/stories/form';
    }

    else {
        storyModal.style.display = 'none';
    }
});

// 이미지 및 삭제 버튼 삽입 함수
function insertImage(src) {
    const imageContainer = document.createElement('div');
    imageContainer.style.position = 'relative';
    imageContainer.style.display = 'inline-block';
    imageContainer.style.marginBottom = '10px';

    const img = document.createElement('img');
    img.src = src;
    img.style.maxWidth = '100%';
    img.style.maxHeight = '400px';
    img.style.display = 'block';
    imageContainer.appendChild(img);

    const removeButton = document.createElement('button');
    removeButton.className = 'remove-button';
    removeButton.style.position = 'absolute';
    removeButton.style.top = '0px';
    removeButton.style.right = '0px';
    removeButton.style.border = 'none';
    removeButton.style.background = 'rgba(22, 22, 22, 0.3)';
    removeButton.style.cursor = 'pointer';
    removeButton.style.padding = '5px';

    const removeIcon = document.createElement('img');
    removeIcon.src = '/images/Trash.png';
    removeIcon.alt = '이미지 삭제';
    removeIcon.style.width = '20px';
    removeIcon.style.height = '20px';
    removeIcon.style.opacity = '1';

    removeButton.appendChild(removeIcon);
    imageContainer.appendChild(removeButton);

    contentEditableDiv.appendChild(imageContainer);
    images.push(img.src);

    removeButton.addEventListener('click', () => {
        contentEditableDiv.removeChild(imageContainer);
        const index = images.indexOf(img.src);
        if (index > -1) {
            images.splice(index, 1);
        }
        console.log(images);
    });
}