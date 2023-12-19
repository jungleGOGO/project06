/*
data-role="node":
각 노드를 식별하는 속성.

data-role="expander":
노드를 확장하거나 축소하는데 사용되는 확장기.

data-mode="open" 또는 data-mode="close":
확장기의 현재 상태를 나타내는 속성.

data-id:
각 노드를 식별하는 고유한 ID.

data-text:
노드에 표시되는 텍스트 내용을 지정하는 속성.

data-selected="true" 또는 data-selected="false":
노드가 선택되었는지의 상태를 나타내는 속성.

data-checked="true" 또는 data-checked="false":
노드의 체크 상태를 나타내는 속성.

data-icon:
노드에 표시되는 아이콘을 지정하는 속성.

data-child-loaded="true" 또는 data-child-loaded="false":
노드의 자식 노드가 로드되었는지의 상태를 나타내는 속성.

data-parent:
현재 노드의 부모 노드를 식별하는 속성.

data-has-children="true" 또는 data-has-children="false":
노드가 자식 노드를 가지고 있는지의 상태를 나타내는 속성.

data-expanded="true" 또는 data-expanded="false":
노드가 초기에 확장된 상태인지를 나타내는 속성.

data-draggable="true" 또는 data-draggable="false":
노드를 드래그 가능하게 할지 여부를 나타내는 속성.

data-droppable="true" 또는 data-droppable="false":
노드를 드롭 가능하게 할지 여부를 나타내는 속성.

data-disabled="true" 또는 data-disabled="false":
노드가 비활성화된 상태인지를 나타내는 속성.

data-additional-data:
노드와 관련된 추가 데이터를 저장하는 데 사용되는 속성.

data-template:
노드에 대한 사용자 지정 HTML 템플릿을 정의하는 속성.

data-children:
노드의 자식 노드를 나타내는 배열이나 객체를 저장하는 속성.

data-user-data:
사용자 정의 데이터를 노드에 연결하는 데 사용되는 속성.

data-href:
노드가 클릭되었을 때 이동할 URL을 지정하는 속성.

data-image:
노드에 사용될 이미지의 경로를 지정하는 속성.

data-image-expander-collapsed:
닫힌 상태의 확장기(expander)에 사용될 이미지의 경로를 지정하는 속성.

data-image-expander-expanded:
열린 상태의 확장기에 사용될 이미지의 경로를 지정하는 속성.

data-image-checkbox-unchecked:
체크박스가 해제된 상태에 사용될 이미지의 경로를 지정하는 속성.

data-image-checkbox-checked:
체크박스가 선택된 상태에 사용될 이미지의 경로를 지정하는 속성.

data-image-checkbox-indeterminate:
체크박스가 중간 상태에 있는 경우 사용될 이미지의 경로를 지정하는 속성.

data-image-drag-helper:
드래그 중에 나타나는 도움말 이미지의 경로를 지정하는 속성.

data-hovered:
마우스가 노드 위에 올려진 상태를 나타내는 속성.

data-selected:
노드가 선택된 상태인지 나타내는 속성.

data-checked:
노드에 체크박스가 있는 경우, 체크된 상태를 나타내는 속성.

 */


/////////////////////////////////////// 모달창 및 사이드메뉴 ////////////////////////////////////////

const icon = document.getElementById('iconNav');
const balloon = document.getElementById('balloon');

const icon2 = document.getElementById('moreNav');
const balloon2 = document.getElementById('balloon2');

const icon3 = document.getElementById('more2');
const balloon3 = document.getElementById('balloon3');

const btn = document.getElementById('popupBtn');
const modal = document.getElementById('modalWrap');
const closeBtn = document.getElementById('closeBtn');


icon.addEventListener('mouseenter', function (){
    balloon.style.display = 'block';
})

icon.addEventListener('mouseover', function (){
    balloon.style.display = 'block';
})

icon.addEventListener('mouseout', function (){
    balloon.style.display = 'none';
})

icon2.addEventListener('click', function (event) {
    if (balloon2.style.display === 'none') {
        balloon2.style.display = 'block';
    } else if (balloon2.style.display === 'block') {
        balloon2.style.display = 'none';
    }
});

icon3.addEventListener('click', function (event) {
    if (balloon3.style.display === 'none') {
        balloon3.style.display = 'block';
    } else if (balloon3.style.display === 'block') {
        balloon3.style.display = 'none';
    }
});

btn.onclick = function() {
    modal.style.display = 'block';
}
closeBtn.onclick = function() {
    modal.style.display = 'none';
}

window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}


//////////////////////////////////// Split //////////////////////////////////////
Split(['#top-pane', '#middle-pane', '#bottom-pane'], {
    direction: 'vertical',
    minSize: [30, 30, 140]
});

Split(['#split', '#view'], {
    sizes: [25,75],
    minSize: [230,0]
});

//마우스 우클릭으로 여는 메뉴
$.contextMenu({
    selector: '[data-role="display"]',
    items: {
    item1: {
    name: '삭제',
    callback: function (key, options) {
    // 메뉴 아이템을 클릭한 경우의 동작
    console.log("key", key);
    console.log("options", options);

    var $trigger = options.$trigger;
    var filename = $trigger.find('a').attr('href')
    // span 안의 a 태그의 텍스트를 가져옴
    console.log("Clicked on " + key + " for element with filename: " + filename);

    axios.post("/editor/delete", { filename: filename }).then((response) => {
    showFileList()
    console.log("삭제됨");
}).catch((error) => {
    console.log(error);
});
}
},
    item2: {
    name: '이름 변경',
    callback: function (key, options) {
    openRenameFileModal(); // 모달 열기
}
}
}
});

<!--파일 확장자를 선택자로 추가해서 파일 만들기-->
    const extensionSelect = document.getElementById("extension"); //select문에서 선택한 값
    const customExtensionInput = document.getElementById("customExtensionInput"); //직접입력 확장자의 div 값 입력한 값
    const customExtension = document.getElementById("customExtension"); //직접입력 확장자로 입력한 input 값
    const downloadNameInput = document.getElementById("downloadName"); //현재출력중인 파일명

    // select문에서 직접입력을 선택했을때 직접입력 input이 보이고 안보이고 기능
    extensionSelect.addEventListener("change", function () {
    if (extensionSelect.value === "") {
    // "직접입력"을 선택했을 때
    customExtensionInput.style.display = "block";
} else {
    // 다른 옵션을 선택했을 때
    customExtensionInput.style.display = "none";
    downloadNameInput.style.display = "block";
}
});

    // 직접 입력 확장자 input에 값을 입력했을때 직접입력 확장자의 값과 확장자 select문의 값이 같도록함.
    customExtension.addEventListener("input", function () {
    // 직접 입력 값을 select의 값으로 설정
    extensionSelect.value = customExtensionInput.value;
    console.log(extensionSelect.value)
    console.log(customExtension.value)
});

<!--다운로드 버튼 작동하는 스크립트-->

    document.getElementById("saveBtn").addEventListener("click", () => {

    let filename = document.getElementById("downloadName").value; //현재출력중인 파일명 값
    let extension = document.getElementById("extension").value; //확장자 값

    // 사용자가 입력한 파일명과 조합된 확장자를 설정
    let fullFilename = filename + extension;

    // 파일 이름이 유효한지 확인
    if (!isValidFilename(filename)) {
    alert("파일명에는 특수 문자 및 일부 예약어를 사용할 수 없습니다.");
    return; // 추가 실행 중단
}

    // 사용자가 입력한 파일명과 조합된 확장자를 설정
    const htmlCode = htmlEditor.value; //html에 입력한 값
    const cssCode = cssEditor.value;  //css에 입력한 값
    const jsCode = jsEditor.value;  //js에 입력한 값
    const content = `<html>\n<head>\n<style>\n${cssCode}\n</style>\n</head>\n<body>\n${htmlCode}\n</body>\n<script>\n${jsCode}\n<\/script>\n</html>`;

//Blob는 binary large object의 약자. content는 BLob에 들어갈 데이터를 담은 배열.
//type은 객체의 특성. text/plain은 텍스트로 생성
    var blob = new Blob([content], { type: "text/plain" });

    //anchor는 a태그를 나타낸다.

    var a = document.createElement("a"); //새로운 a태그 생성
    a.style.display = "none";
    a.href = window.URL.createObjectURL(blob); //blob객체를 url로 변환, a요소의 href로 설정
        //blob로 href를 설정한 이유는 바이너리 데이터(0,1 이진형태로 표현되는 데이터)(이미지, 파일등)을 다룰때 blob를 사용하기 떄문이다.
    a.download = fullFilename; //다운로드할때 로컬에 저장될 파일의 이름

    document.body.appendChild(a); // <a>요소를 문서의 body에 추가합니다.
    a.click(); // 클릭하면 링크클릭한것처럼 동작.

    window.URL.revokeObjectURL(a.href); // createObjectUrl을 통해 생성된 URL을 해제(revoke)한다. Blob Url을 생성하면 브라우저는 이를 메모리에 유지함.
        //이 코드는 그 url을 브라우저에게 알려 더 이상 필요하지 않다고 알려 메모리에서 해제한다. blob url을 사용한 후 메모리 누수 방지위함이다
    document.body.removeChild(a); // 더 이상 필요하지 않은 DOM요소를 삭제하여 페이지의 가독성을 유지, 메모리 효율적 관리에 도움된다.
    //DOM은 Document Object Model의 약자. html 문서의 전체구조를 표현하는 모델.
    });

<!--파일이름에 사용할수 없는 문자들-->
    function isValidFilename(filename) {
    // 사용할 수 없는 문자와 규칙을 정의합니다.
    const invalidChars = /[\/:*?"<>|.]/;
    const invalidNames = ['con', 'prn', 'aux', 'nul', 'com1', 'com2', 'com3', 'com4', 'com5', 'com6', 'com7', 'com8', 'com9', 'lpt1', 'lpt2', 'lpt3', 'lpt4', 'lpt5', 'lpt6', 'lpt7', 'lpt8', 'lpt9'];

    // 사용자가 입력한 파일 이름을 확인합니다.
    if (!filename || invalidChars.test(filename) || invalidNames.includes(filename.toLowerCase())) {
    return false;
}

    return true;
}

<!--파일 이름 변경시 사용할수 없는 문자-->
    function isValidFilename2(newfilename) {
    // 사용할 수 없는 문자와 규칙을 정의합니다.
    const invalidChars = /[\/:*?"<>|.]/;
    const invalidNames = ['con', 'prn', 'aux', 'nul', 'com1', 'com2', 'com3', 'com4', 'com5', 'com6', 'com7', 'com8', 'com9', 'lpt1', 'lpt2', 'lpt3', 'lpt4', 'lpt5', 'lpt6', 'lpt7', 'lpt8', 'lpt9'];

    // 사용자가 입력한 파일 이름을 확인합니다.
    if (!newfilename || invalidChars.test(newfilename) || invalidNames.includes(newfilename.toLowerCase())) {
    return false;
}

    return true;
}

<!--저장버튼 작동하는 스크립트-->

    document.getElementById("save").addEventListener("click", function () {
    let extension = document.getElementById("extension").value;
    let name =document.getElementById("filename").value; // 저장하고 싶은 파일의 파일명 값
    let filename = name + extension ;
    const htmlCode = htmlCodeEl.value;
    const cssCode = cssCodeEl.value;
    const jsCode = jsCodeEl.value;
    const content = `<html>\n<head>\n<style>\n${cssCode}\n</style>\n</head>\n<body>\n${htmlCode}\n</body>\n<script>\n${jsCode}\n<\/script>\n</html>`;

    if (!isValidFilename(name)) {
    alert("파일명에는 특수 문자 및 일부 예약어를 사용할 수 없습니다.");
    return; // 추가 실행 중단
}
    //code라는 변수 선언, 이 변수에 객체 할당(중괄호로 객체생성함.). 객체의 각 속성은 filename, content.
    let code = {'filename' : filename,'content' : content };

    axios.post("/editor/get", code) // code 객체를 전달
    .then((response) => {
    alert("파일이 성공적으로 저장되었습니다");
})
    .catch((error) => {
    if (error.response && error.response.data) {
    if (error.response.data.msg) {
    alert(error.response.data.msg);
} else {
    alert(error.response.data);
}
} else {
    console.error("에러:", error.message);
}
});
});

<!--불러오기 버튼-->
// DOMContentLoaded는 문서의 초기 html 구조가 완전히 로드되고 파싱되었을때 발생하는 이벤트-
// 스타일시트(style태그), 하위프레임(iframe)등의 외부 리소스가 아직 로드되지 않아도 발생.
    document.addEventListener('DOMContentLoaded', function() {
    // 초기 파일 목록 불러오기
    loadFileList();
});

<!--저장소 모달창 작동 스크립트-->

    // "불러오기" 버튼 클릭 시 모달 열기
    function showFileList() {
    saveTreeState();
    $('#tree').remove();
    loadFileList();
    // 파일 목록을 불러오는 함수 호출
    // 모달 열기
    openModal();
}

    //저장소 모달 열기 함수
    function openModal() {
    document.getElementById('fileListModal').style.display = 'block';
}

    //저장소 모달 닫기 함수
    function closeModal() {
    document.getElementById('fileListModal').style.display = 'none';
}

<!--저장소에서 폴더 열림 닫힘 상태 저장하는 스크립트-->
//node는 트리구조에서의 기본단위다. 각 노드는 특정한 데이터를 나타내고 이 데이터 간에 부모-자식 관계가 있따.
//트리에서 각각의 데이터를 담고 있는 각 요소를 노드라고 부른다.
//트리의 최상위 노드를 루트 노드(Root Node)라고 한다.

    let savedState = [];
    function saveTreeState() {
    savedState = [];
    $('#tree').find('[data-role="node"]').each(function() {
    let expander = $(this).find('[data-role="expander"]');//트리에서 노드를 확장하거나 축소하는 확장기(expander)를 식별하는데 활용
    if (expander.attr('data-mode') === 'open') {
    savedState.push($(this).attr('data-id')); // 열린 노드의 id를 savedState배열에 추가
}
});
}

    // data-role="node"는 각 노드를 식별하기위함. 특정 html요소가 트리에서 노드로 간주되도록 지정.
    function restoreTreeState() {
    $('#tree').find('[data-role="node"]').each(function() {
        let node = $(this); //현재 순회중인 노드를 jquery 객체로 저장
        let id = node.attr('data-id'); // 현재 노드의 id를 가져와서 변수 id에 저장

        if (savedState.includes(id)) { //savedstate에 현재 노드의 id가 포함되어 있는지 확인
            // 노드가 열려 있을 경우, 해당 노드의 확장기(expander)의 data-mode 속성을 'open'으로 설정하고, 아이콘의 클래스를 변경하여 열린 상태를 시각적으로 나타낸다
            node.find('[data-role="expander"]').attr('data-mode', 'open').find('i.gj-icon').removeClass('chevron-right').addClass('chevron-down');
            //노드가 열려 있을경우 하위 ul요소를 보이도록 설정
            node.children('ul').show();
        } else {
            node.find('[data-role="expander"]').attr('data-mode', 'close').find('i.gj-icon').removeClass('chevron-down').addClass('chevron-right');
            node.children('ul').hide();
        }
    });
}
    // 페이지 로드 시에 실행되도록
    function loadFileList() {
    axios.get('/editor/fileList').then(response => {
        const fileList = response.data;
        // 이미 트리가 있는지 여부 확인하고 없다면 새로운 div요소를 생성하여 해당요소에 트리뷰 추가
        if ($('#pane').find('#tree').length === 0) {
            $('#pane').append('<div id="tree"></div>');
        }


        // 새로운 파일 목록으로 트리뷰 재구성
        $('#tree').tree({
            primaryKey: 'id',
            uiLibrary: 'materialdesign',
            dataSource: transformToTreeViewFormat(fileList),
            imageUrlField: 'flagUrl'
        });

        // 트리 재구성 후 상태 복원
        restoreTreeState();
        treeEvent();
    }).catch(error => {
        console.error('Error fetching file list:', error);
    });
}

    // FileNode 객체를 트리뷰 형식으로 변환
    function transformToTreeViewFormat(fileNode) {
    var treeData = [];
    convertNode(fileNode, treeData, 1); // 재귀적으로 노드를 변환합니다. 재귀적- 함수내에서 같은 함수를 호출하는것
    return treeData;
}

    // FileNode 객체를 트리뷰 노드로 변환
    function convertNode(fileNode, treeData, nodeId) {
        //새로운 노드 객체를 생성
    var node = {
    id: nodeId, // 노드의 고유한 식별자
    text: "<a href='" + fileNode.text + "'>" + fileNode.name + "</a>", //트리에 표시될 텍스트
    flagUrl: fileNode.flagUrl, // 노드에 대한 이미지 url
    children: [] //자식 노드들을 저장할 배열
};

    fileNode.children.forEach(function (child) {
    convertNode(child, node.children, nodeId + 1);
    nodeId++;
});

    treeData.push(node);
}

    //html , css, jscode값을 분배하는 코드
    function setCodeValues(htmlCode, cssCode, jsCode) {

    // 코드 미러 에디터에 값 설정
    if (htmlCode.trim() !== '') {
    htmlEditor.setValue(htmlCode);

} else {
    // 기존 내용이 있으면 이전 내용을 유지합니다.
    htmlEditor.setValue(htmlEditor.getValue());

}

    if (cssCode.trim() !== '') {
    cssEditor.setValue(cssCode);

} else {
    cssEditor.setValue(cssEditor.getValue());
}

    if (jsCode.trim() !== '') {
    jsEditor.setValue(jsCode);

} else {
    jsEditor.setValue(jsEditor.getValue());

}

}
    function treeEvent() {
    const treeArea = document.querySelector('#tree');
    // a태그일 경우 href로 이동하는 이벤트를 막음
    treeArea.addEventListener('click', function(event) {
    if (event.target.closest('a')) {
    event.preventDefault();
}
});
    //컨텍스트 메뉴를 여는 이벤트. 우클릭시 해당 파일의 href값을 rehandleFileSelection의 reselected에 저장.
    treeArea.addEventListener('contextmenu', function(event) {
    const anchor2 = event.target.closest('a');
    if (anchor2) {
    event.preventDefault();
    const filename = anchor2.textContent.trim();
    handleFileSelection(filename);
    rehandleFileSelection(anchor2.getAttribute("href"));
    console.log("파일폴더:"+rehandleFileSelection(anchor2.getAttribute("href")));
}
});
    treeArea.addEventListener('dblclick', function(event) {
    const anchor = event.target.closest('a');
    const folderAndfile = anchor.getAttribute('href');
    htmlEditor.setValue("");
    cssEditor.setValue("");
    jsEditor.setValue("");
    if (anchor) {
    // 파일명 추출
    const filename = anchor.textContent; // 파일명 추출
    document.getElementById("downloadName").value = filename;
    console.log("filename")
    console.log(filename)
    axios.post('/editor/test2', null, {
    params: { filename2: folderAndfile }
})
    .then(response => {
    const fileContent = response.data;
    const codeEl = document.querySelector("[data-code]").contentWindow.document;
    const matchResult = fileContent.match(/<style>([\s\S]*?)<\/style>|<body>([\s\S]*?)<\/body>|<script>([\s\S]*?)<\/script>/g);

    if (matchResult) {
    const [styleMatch, bodyMatch, scriptMatch] = matchResult;

    const cssCode = styleMatch ? styleMatch.replace(/<style>|<\/style>/g, '').trim() : '';
    const htmlCode = bodyMatch ? bodyMatch.replace(/<body>|<\/body>/g, '').trim() : '';
    const jsCode = scriptMatch ? scriptMatch.replace(/<script>|<\/script>/g, '').trim() : '';

    setCodeValues(htmlCode, cssCode, jsCode);
} else {
    console.log('No match found in the HTML file.');
}
})
    .catch(error => console.error('Error fetching file content:', error))
    .finally(() => {
    // 더블클릭 시 모달을 닫도록 추가
    closeModal();
});
}
});
    treeArea.addEventListener('dblclick', function(event) {
    const anchor = event.target.closest('a');
    if (anchor) {
    const wrapper = anchor.closest('[data-role="wrapper"]');
    const expander = wrapper.querySelector('[data-role="expander"]');
    const childUl = wrapper.parentElement.querySelector('ul');

    if (expander && childUl) {
    const currentMode = expander.getAttribute('data-mode');
    if (currentMode === 'open') {
    expander.setAttribute('data-mode', 'close');
    childUl.style.display = 'none';
} else {
    expander.setAttribute('data-mode', 'open');
    childUl.style.display = 'block';
}
}
}
});

}

<!--선택한 파일 이름을 받음-->
    let selectedFile = null;

    function handleFileSelection(filename) {
    selectedFile = filename;
    // 필요에 따라 추가적인 로직을 수행할 수 있습니다.
}

<!--우클릭으로 이름변경시 값을 추출-->
    let reselectedFile = null;

    function rehandleFileSelection(anchor2) {
    if (selectedFile) {
    reselectedFile = anchor2;

    return reselectedFile;
    // 선택한 파일에 대한 추가적인 로직을 수행할 수 있습니다.
}
}

<!--파일 이름 바꾸는 스크립트-->
    // 모달 열기 함수
    function openRenameFileModal() {
    const selectedFile = getSelectedFile();
    if (selectedFile) {
    document.getElementById('selectedFile').value = selectedFile;
    document.getElementById('renameFileModal').style.display = 'block';
} else {
    alert('파일을 선택해주세요.');
}
}

    // 현재 선택된 파일의 이름을 가져오는 함수 (수정이 필요할 수 있음)
    function getSelectedFile() {
    if (selectedFile) {
    return selectedFile;
} else {
    const selectedNode = document.querySelector('#tree [data-role="node"][data-selected="true"]');
    if (selectedNode) {
    return selectedNode.textContent.trim();
} else {
    return null;
}
}
}

    // 모달 닫기 함수
    function closeRenameFileModal() {
    document.getElementById('renameFileModal').style.display = 'none';
}

    // 파일 이름 변경 함수
    function renameFile() {
    const reextensionSelect = document.getElementById("reextension"); // 파일확장자 값
    const currentFilename = document.getElementById("selectedFile").value; // 현재 파일이름명 값
    const newFileSet = document.getElementById("newFilename").value; //새로 입력한 파일이름 값 (파일이름명 규칙 때문에 따로 만듬)
    const newFilename = document.getElementById("newFilename").value+reextensionSelect.value; // 새로입력한 파일이름 + 확장자값
    const currentFileAndFolder = reselectedFile; // 선택한 파일의 href값

    //파일이름명 규칙 실행
    if (!isValidFilename2(newFileSet)) {
    alert("파일명에는 특수 문자 및 일부 예약어를 사용할 수 없습니다.");
    return; // 추가 실행 중단
}
    // 마지막 역슬래시 이후의 경로 부분 추출
    const lastBackslashIndex = currentFileAndFolder.lastIndexOf("\\");
    //href값에서 뒤의 파일명은 제거한 폴더 경로값만 추출
    const currentFolder = lastBackslashIndex !== -1 ? currentFileAndFolder.substring(0, lastBackslashIndex + 1) : "";


    // 파일을 백엔드에서 이름을 변경하도록 AJAX 요청을 보냅니다.
    axios.post("/editor/rename", null, {
    params: {
    currentFilename: currentFilename,
    newFilename: newFilename,
    currentFolder: currentFolder
}
})
    .then((response) => {
    showFileList();
    closeRenameFileModal();
    alert("파일명 변경 완료");
})
    .catch((error) => {
    console.error("에러 응답:", error.response); // 에러 응답 출력
    if (error.response && error.response.data) {
    alert(error.response.data);
} else {
    console.error("에러:", error.message);
}
});
}

    const reextensionSelect = document.getElementById("reextension");
    const reFileNameInput = document.getElementById("reselectedFile");
    const currentFilename = document.getElementById("selectedFile").value;
    const newFilename = document.getElementById("newFilename").value;
    const recustomExtension = document.getElementById("recustomExtensionInput");
    const recustomEx= document.getElementById("recustomExtension");



    // "직접입력" 옵션 선택 시 처리
    reextensionSelect.addEventListener("change", function () {
    if (reextensionSelect.value === "") {
    recustomExtension.style.display = "block";
} else {
    recustomExtension.style.display = "none";
    reFileNameInput.style.display = "block";

}
});
//     //
//     // // "직접입력" 값이 변경될 때 처리
//     // recustomExtension.addEventListener("input", function () {
//     // // 직접 입력 값을 select's value로 설정
//     // reextensionSelect.value = recustomEx.value;
// });


<!--링크는 새창으로 띄우게함-->

    document.addEventListener('DOMContentLoaded', function () {
    // Wait for the iframe to load
    document.getElementById('content').addEventListener('load', function () {
        // Access the contentDocument of the iframe
        const iframeContent = document.getElementById('content').contentDocument;
        // Check if contentDocument is available
        if (iframeContent) {
            // Find all 'a' tags and set target="_blank"
            const anchorTags = iframeContent.querySelectorAll('a');
            anchorTags.forEach(function (anchor) {
                anchor.setAttribute('target', '_blank');
            });
        }
    });
});


<!--자동저장 기능-->

    function autosave () {
    let extension = document.getElementById("extension").value;
    let name =document.getElementById("filename").value; // 저장하고 싶은 파일의 파일명 값
    let filename = name + extension ;

    // const htmlCode = htmlEditor.value;
    // const cssCode = cssEditor.value;
    // const jsCode = jsEditor.value;
    const content = `<html>\n<head>\n<style>\n${cssCode}\n</style>\n</head>\n<body>\n${htmlCode}\n</body>\n<script>\n${jsCode}\n<\/script>\n</html>`;
    let local = setInterval(saveToLocalStorage, 4000);

    if (!isValidFilename(name)) {
    alert("파일명에는 특수 문자 및 일부 예약어를 사용할 수 없습니다.");
    return; // 추가 실행 중단
}
    let code = {'filename' : filename,'content' : content };

    axios.post("/editor/autoSave", code)
    .then((response) => {
    // 서버 응답이 성공한 경우
    showSuccessMessage();
    clearInterval(local);
    localStorage.removeItem('htmlContent');
    localStorage.removeItem('cssContent');
    localStorage.removeItem('jsContent');
})
    .catch((error) => {
    if (error.response && error.response.data) {
    if (error.response.data.msg) {
    alert(error.response.data.msg);
} else {
    alert(error.response.data);
}
} else {
    console.error("에러:", error.message);
}
});

}
    // 성공 메시지를 표시하고 2초 후에 숨김
    function showSuccessMessage() {
    const checkCircle = document.getElementById('checkCircle');

    // 성공 메시지를 표시
    checkCircle.style.display = 'inline-block';

    // 2초 후에 다시 숨김
    setTimeout(() => {
    checkCircle.style.display = 'none';
}, 2000);
}


<!--로컬 스토리지에 저장하는 스크립트-->

    function saveToLocalStorage() {

    localStorage.setItem('htmlContent', htmlEditor.getValue());
    localStorage.setItem('cssContent', cssEditor.getValue());
    localStorage.setItem('jsContent', jsEditor.getValue());
}

    // Load content from local storage on page load


    htmlCode= localStorage.getItem('htmlContent') || '';
    cssCode= localStorage.getItem('cssContent') || '';
    jsCode= localStorage.getItem('jsContent') || '';



    document.addEventListener('DOMContentLoaded', function() {
    setInterval(saveToLocalStorage,7000);
    // HTML textarea


    const htmlTextarea = document.getElementById('htmlTextarea');
    let savedHtmlContent = localStorage.getItem('htmlContent');

    // CSS textarea

    const cssTextarea = document.getElementById('cssTextarea');
    let savedCssContent = localStorage.getItem('cssContent');

    // JS textarea

    const jsTextarea = document.getElementById('jsTextarea');
    let savedJsContent = localStorage.getItem('jsContent');

    // 코드가 어디에 어떻게 박히느지 몰라서 추가함..

    // 빈 값인 경우에만 이어서 작업 여부 확인
    if (!savedHtmlContent && !savedCssContent && !savedJsContent) {
    return; // 모든 값이 비어있으면 확인 창을 표시하지 않음
}
    // Display a confirmation dialog with an OK and Cancel button
    var userConfirmed = window.confirm("이전에 저장하지 못한 기록이 있습니다:\n\n 이어서 작업하시겠습니까?");

    // If the user clicks OK, populate textareas with saved values
    if (userConfirmed) {
    if (savedHtmlContent !== null) {
    htmlTextarea.value = savedHtmlContent;
    // htmlCodeEl.value=savedHtmlContent;
    htmlEditor.setValue(savedHtmlContent);
}

    if (savedCssContent !== null) {
    // cssCodeEl.value=savedCssContent;
    cssTextarea.value = savedCssContent;
    cssEditor.setValue(savedCssContent);
}

    if (savedJsContent !== null) {
    // jsCodeEl.value = savedJsContent;
    jsTextarea.value=savedJsContent;
    jsEditor.setValue(savedJsContent);
}
} else { // If the user clicks Cancel, clear the values in the textareas
    // htmlCodeEl.value="";
    // cssCodeEl.value="";
    // jsCodeEl.value="";
    htmlEditor.value="";
    cssEditor.value="";
    jsEditor.value="";
    localStorage.removeItem('htmlContent');
    localStorage.removeItem('cssContent');
    localStorage.removeItem('jsContent');
}});

    const checkbox = document.getElementById("autoSaveCheck");
    let auto; // 변수 선언


    checkbox.addEventListener("change", function() {
    if (checkbox.checked) {
    clearInterval(auto);
    console.log("자동저장중");

    auto = setInterval(autosave,4000);


} else {
    clearInterval(auto);
    console.log("자동저장해제");
    setInterval(saveToLocalStorage, 4000);
}
});

<!--파일업로드 관련 스크립트-->

    document.getElementById('readFile').addEventListener('click', function () {
    // 파일 업로드 input 엘리먼트 생성
    var inputFile = document.createElement('input');
    inputFile.type = 'file';
    inputFile.accept = '.txt,.css,.html,.js,.scss';
    inputFile.maxSize = 10485760;
    inputFile.name = 'upfile';

    // 파일이 선택되었을 때의 이벤트 핸들러
    inputFile.addEventListener('change', function () {
    // FormData 객체 생성
    var formData = new FormData();
    // 선택된 파일 추가
    formData.append('file', this.files[0]);

    // 파일 업로드를 위한 Axios 요청
    axios.post('/editor/readFile', formData, {
    headers: {
    'Content-Type': 'multipart/form-data',
}
})
    .then(function (response) {
    // 서버 응답에서 코드 블록 추출
    const { cssCode, htmlCode, jsCode } = extractCodeBlocks(response.data);
    // 추출된 코드 블록을 에디터에 설정
    setCodeValues(htmlCode, cssCode, jsCode);
})
    .catch(function (error) {
    console.error('Error:', error);
});
});

    // 생성한 input 엘리먼트를 숨김 처리
    inputFile.style.display = 'none';

    // body에 input 엘리먼트 추가
    document.body.appendChild(inputFile);

    // input 엘리먼트 클릭 (파일 업로드 다이얼로그를 엽니다.)
    inputFile.click();
});

    function extractCodeBlocks(fileContent) {
    // 줄바꿈 문자열을 모두 표준화하여 \n으로 변환
    fileContent = fileContent.replace(/\r\n/g, '\n').replace(/\r/g, '\n');

    const headRegex = /<head>([\s\S]*?)<\/head>/g;
    const bodyRegex = /<body>([\s\S]*?)<\/body>/g;
    const styleRegex = /<style>([\s\S]*?)<\/style>/g;
    const scriptRegex = /<script[^>]*>([\s\S]*?)<\/script>/g;

    let bodyCode = '';
    let headCode = '';
    let cssCode = '';
    let htmlCode = '';
    let jsCode = '';
    let match;
    // <script> 태그 일치 항목 찾기
    while ((match = scriptRegex.exec(fileContent)) !== null) {
    const scriptContent = match[1].replace(/^\s*\n/gm, ''); // 첫 번째 빈 줄 제거
    jsCode += scriptContent.trim() + '\n';
}

// jsCode에서 선행 및 후행 빈 줄 제거
    jsCode = jsCode.replace(/(^\s*|\s*$)/g, '');

    fileContent = fileContent.replace(/<script[^>]*>[\s\S]*?<\/script>/g, '');
    // <body> 태그 일치 항목 찾기
    while ((match = bodyRegex.exec(fileContent)) !== null) {
    htmlCode += match[1].trim() + '\n';
}
    fileContent = fileContent.replace(/<body>[\s\S]*?<\/body>/g, '');

    // <style> 태그 일치 항목 찾기

    while ((match = styleRegex.exec(fileContent)) !== null) {
    cssCode += match[1].trim() + '\n';
}

    return { cssCode, htmlCode, jsCode };
}

//////////////////////////////////// 폴더 생성 //////////////////////////////////////
// 폴더명 입력 문자만 입력 가능하도록 처리
function validateInput(input) {
    const validChars = /^[가-힣a-zA-Z0-9]+$/; // 한글, 영어, 숫자만 허용하는 정규 표현식

    // 입력된 값이 유효한 문자만 포함하고 있는지 검사합니다.
    if (!validChars.test(input.value)) {
        alert("한글, 영어, 숫자만 입력할 수 있습니다.");
        input.value = input.value.replace(/[^가-힣a-zA-Z0-9]/g, ''); // 유효하지 않은 문자 제거
    }
}
// 선택한 항목이 디렉토리라면 해당하는 디렉토리 하위에 디렉토리를 생성하고, 파일이면 그 파일이있는 경로에 디렉토리를 생성
document.getElementById("mkdir2").addEventListener("click", function() {
    saveTreeState();
    let mkdirnameInput = document.getElementById("mkdirname");

    // validateInput 함수를 사용하여 입력 검증
    validateInput(mkdirnameInput);

    const selectedElement = document.querySelector('[data-selected="true"]');

    var anchor;
    var href;
    if (selectedElement) {
        // data-id 값을 추출
        const dataId = selectedElement.getAttribute('data-id');

        // 선택된 요소 내부의 a 태그
        anchor = selectedElement.querySelector('a');
        href = anchor ? anchor.getAttribute('href') : null;

        console.log('Data ID:', dataId);
        console.log('Href:', href);
    } else {
        console.log('선택된 요소가 없습니다.');
    }

    //작성한 폴더명
    let mkdirname = document.getElementById("mkdirname").value;
    //현재 선택된 경로
    let path = href;
    let dir = { 'mkdirname': mkdirname, 'path': path };
    axios.post("/api/mkdir", dir).then((response) => {
        // 저장 후 파일 목록 다시 불러오기
        $('#tree').remove(); // 트리를 완전히 제거합니다.
        loadFileList();
        modal.style.display = 'none';

    }).catch((error) => {
        console.log(error);
    });
});



