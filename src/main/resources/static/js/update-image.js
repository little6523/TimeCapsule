const contentEditableDiv = document.getElementById('content');
const imageUploadInput = document.getElementById('imageUpload');
const uploadBtn = document.getElementById('uploadBtn');

// 기존 이미지, 콘텐츠 로드
// const existingImages = /*[[${images}]]*/ {};
// const content = /*[[${story.content}]]*/ '';
window.deletedImages = [];

let beforeEditContent = `<p>${content}</p>`;

Object.entries(existingImages).forEach(([key, value]) => {
    insertImage(value, true);
});

contentEditableDiv.innerHTML += beforeEditContent;

// 이미지 업로드 버튼 클릭 시 파일 선택창 열기
uploadBtn.addEventListener('click', function () {
    imageUploadInput.click();
})

// 파일 선택 후 이미지 추가
imageUploadInput.addEventListener('change', function () {
    const files = Array.from(imageUploadInput.files);

    files.forEach(file => {
        const reader = new FileReader();
        reader.onload = e => insertImage(e.target.result, false);
        reader.readAsDataURL(file);
    })
});

// 페이지 로드 시 기존 이미지에 삭제 버튼 추가
window.onload = function () {
    const images = contentEditableDiv.querySelectorAll('img');
    images.forEach(function (img) {
        const imageContainer = img.parentElement;

        // 기존 이미지 삭제 버튼 추가
        const removeButton = createRemoveButton(); // 아래에 removeButton 생성 함수
        imageContainer.appendChild(removeButton);

        // 삭제 버튼 클릭 이벤트 등록
        removeButton.addEventListener('click', function (e) {
            e.preventDefault();
            contentEditableDiv.removeChild(imageContainer); // 이미지 컨테이너 제거
            const imgSrc = img.src; // 이미지의 src 값 가져오기
            const key = findKeyByValue(existingImages, imgSrc.substring(18));
            deletedImages.push(key);
        });
    });
};

function findKeyByValue(obj, value) {
    const values = Object.values(obj);  // 객체의 값 배열
    const index = values.indexOf(value);  // 값의 인덱스 찾기
    if (index !== -1) {
        return Object.keys(obj)[index];  // 인덱스를 기반으로 키 반환
    }
    return null;
}

// 이미지 삭제 버튼 생성 함수
function createRemoveButton() {
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

    return removeButton;
}

function insertImage(src, isExisted) {
    const imageContainer = document.createElement('div');
    imageContainer.style.position = 'relative';
    imageContainer.style.display = 'inline-block';
    imageContainer.style.marginBottom = '10px';

    const img = document.createElement('img');
    if (isExisted) {
        img.src = `data:image;base64,${src}`;
    } else {
        img.src = src;
    }
    img.style.maxWidth = '100%';
    img.style.maxHeight = '400px';
    img.style.display = 'block';
    imageContainer.appendChild(img);

    const removeButton = createRemoveButton();
    imageContainer.appendChild(removeButton);

    contentEditableDiv.appendChild(imageContainer);

    removeButton.addEventListener('click', function (e) {
        contentEditableDiv.removeChild(imageContainer);
    });
}