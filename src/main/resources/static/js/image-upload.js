const imageUploadInput = document.getElementById('imageUpload');
const uploadBtn = document.getElementById('uploadBtn');

// 기존 이미지, 콘텐츠 로드
const content = contentEditableDiv.innerText;
const images = [];

// 이미지 업로드 버튼 클릭 시 파일 선택창 열기
uploadBtn.addEventListener('click', function () {
    imageUploadInput.click();
})

// 파일 선택 후 이미지 추가
imageUploadInput.addEventListener('change', function () {
    const files = Array.from(imageUploadInput.files);

    files.forEach(file => {
        const reader = new FileReader();
        reader.onload = e => insertImage(e.target.result);
        reader.readAsDataURL(file);
    });

    imageUploadInput.value = '';
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