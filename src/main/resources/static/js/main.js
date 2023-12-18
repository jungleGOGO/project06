/////////////////////////////////////// 모달창 및 사이드메뉴 ////////////////////////////////////////

const icon = document.getElementById('iconNav');
const balloon = document.getElementById('balloon');

const icon2 = document.getElementById('moreNav');
const balloon2 = document.getElementById('balloon2');

const btn = document.getElementById('popupBtn');
const modal = document.getElementById('modalWrap');
const closeBtn = document.getElementById('closeBtn');

icon.addEventListener('mouseenter', function (){
  balloon.style.display = 'block';
})

icon.addEventListener('mouseover', function (){
  console.log("bye");
  balloon.style.display = 'block';
})

icon.addEventListener('mouseout', function (){
  balloon.style.display = 'none';
})

icon2.addEventListener('mouseenter', function (){
  balloon2.style.display = 'block';
})

icon2.addEventListener('mouseover', function (){
  balloon2.style.display = 'block';
})

icon2.addEventListener('mouseout', function (){
  balloon2.style.display = 'none';
})

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



const htmlCodeEl = document.querySelector("[data-html]");
const cssCodeEl = document.querySelector("[data-css]");
const jsCodeEl = document.querySelector("[data-js]");
const codeEl = document.querySelector("[data-code]").contentWindow.document;
const runButtonEl = document.querySelector("#run");
const clearButtonEl = document.querySelector("#clear");
const clearHtmlButtonEl = document.querySelector("#clearHtml");
const clearCssButtonEl = document.querySelector("#clearCss");
const clearJsButtonEl = document.querySelector("#clearJs");

const defaultEditorSettings = {
  styleActiveLine: true,
  lineNumbers: true,
  matchBrackets: true,
  tabSize: 2,
  indentUnit: 2,
  theme: "monokai",
  lineWrapping: true,
};

const jsEditor = CodeMirror.fromTextArea(jsCodeEl, {
  ...defaultEditorSettings,
  mode: "javascript",
});

const cssEditor = CodeMirror.fromTextArea(cssCodeEl, {
  ...defaultEditorSettings,
  mode: "css",
});

const htmlEditor = CodeMirror.fromTextArea(htmlCodeEl, {
  ...defaultEditorSettings,
  mode: "xml",
  tags: {
    style: [
      ["type", /^text\/(x-)?scss$/, "text/x-scss"],
      [null, null, "css"],
    ],
    custom: [[null, null, "customMode"]],
  },
});

for (const editor of [jsEditor, cssEditor, htmlEditor]) {
  editor.on("blur", (codeMirror) => {
    codeMirror.save();
  });
}

runButtonEl.addEventListener("click", () => {
  const htmlCode = htmlEditor.getValue();
  const cssCode = cssEditor.getValue();
  const jsCode = jsEditor.getValue();

  codeEl.open();

  // CSS 코드 추가 (줄 바꿈 포함)
  codeEl.write(`\n<head>\n<style>\n${cssCode}\n</style>\n</head>`);

  // HTML 코드 추가 (줄 바꿈 포함)
  codeEl.write(`\n<body>\n${htmlCode}</body>\n`);

  // JavaScript 코드 추가 (줄 바꿈 포함)
  codeEl.write(`<script>\n${jsCode}\n</script>\n`);

  codeEl.close();
});


const closeChars = new Map([
  ['{', '}'],
  ['[', ']'],
  ['(', ')'],
  ['<', '>'],
  ['"', '"'],
  ["'", "'"]
]);

htmlCode =  document.querySelector("[data-html]");
htmlCode.addEventListener('input', function (e) {
  if (j != 1) {
    const pos = e.target.selectionStart;
    const val = [...e.target.value];
    const char = val.slice(pos - 1, pos)[0];// suppose (
    const closeChar = closeChars.get(char);
    if (closeChar) {
      val.splice(pos, 0, closeChar);
      e.target.value = val.join('');
      e.target.selectionEnd = pos;
    }
  }
  j = 0;
});

cssCode = document.querySelector("[data-css]");
cssCode.addEventListener('input', function (e) {
  if (j != 1) {
    const pos = e.target.selectionStart;
    const val = [...e.target.value];
    const char = val.slice(pos - 1, pos)[0];
    const closeChar = closeChars.get(char);
    if (closeChar) {
      val.splice(pos, 0, closeChar);
      e.target.value = val.join('');
      e.target.selectionEnd = pos;
    }
  }
  j = 0;
});

javascriptCode = document.querySelector("[data-js]");
javascriptCode.addEventListener('input', function (e) {
  if (j != 1) {
    const pos = e.target.selectionStart;
    const val = [...e.target.value];

    const char = val.slice(pos - 1, pos)[0];
    const closeChar = closeChars.get(char);

    if (closeChar) {
      val.splice(pos, 0, closeChar);
      e.target.value = val.join('');
      e.target.selectionEnd = pos;
    }
  }
  j = 0;
});


clearButtonEl.addEventListener("click", () => {
  htmlEditor.setValue("");
  cssEditor.setValue("");
  jsEditor.setValue("");
});

clearHtmlButtonEl.addEventListener("click", () => {
  htmlEditor.setValue("");

});
clearCssButtonEl.addEventListener("click", () => {
  cssEditor.setValue("");
});
clearJsButtonEl.addEventListener("click", () => {
  jsEditor.setValue("");
});



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
  let fullFilename = filename.split('.')[0] + extension;
  console.log(fullFilename);
  console.log(filename.split('.')[0]);
  console.log(extension);
  // 파일 이름이 유효한지 확인
  if (!isValidFilename(filename.split('.')[0])) {
  alert("파일명에는 특수 문자 및 일부 예약어를 사용할 수 없습니다.");
  return; // 추가 실행 중단
}

  // 사용자가 입력한 파일명과 조합된 확장자를 설정
  const htmlCodeEl = document.querySelector("[data-html]");
  const cssCodeEl = document.querySelector("[data-css]");
  const jsCodeEl = document.querySelector("[data-js]");
  const htmlCode = htmlCodeEl.value;
  const cssCode = cssCodeEl.value;
  const jsCode = jsCodeEl.value;
  const content = `<html>\n<head>\n<style>\n${cssCode}\n</style>\n</head>\n<body>\n${htmlCode}\n</body>\n<script>\n${jsCode}\n<\/script>\n</html>`;


  var blob = new Blob([content], { type: "text/plain" });

  var a = document.createElement("a");
  a.style.display = "none";
  a.href = window.URL.createObjectURL(blob);
  a.download = fullFilename;

  document.body.appendChild(a);
  a.click();

  window.URL.revokeObjectURL(a.href);
  document.body.removeChild(a);
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
  const htmlCodeEl = document.querySelector("[data-html]");
  const cssCodeEl = document.querySelector("[data-css]");
  const jsCodeEl = document.querySelector("[data-js]");
  const htmlCode = htmlCodeEl.value;
  const cssCode = cssCodeEl.value;
  const jsCode = jsCodeEl.value;
  const content = `<html>\n<head>\n<style>\n${cssCode}\n</style>\n</head>\n<body>\n${htmlCode}\n</body>\n<script>\n${jsCode}\n<\/script>\n</html>`;

  if (!isValidFilename(name)) {
  alert("파일명에는 특수 문자 및 일부 예약어를 사용할 수 없습니다.");
  return; // 추가 실행 중단
}
  let code = {'filename' : filename,'content' : content };
  console.log(filename);
  console.log(content);

  axios.post("/editor/get", code)
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

  // 모달 열기 함수
  function openModal() {
  document.getElementById('fileListModal').style.display = 'block';
}

  // 모달 닫기 함수
  function closeModal() {
  document.getElementById('fileListModal').style.display = 'none';
}

<!--저장소에서 폴더 열림 닫힘 상태 저장하는 스크립트-->

  let savedState = [];
  function saveTreeState() {
  savedState = [];
  $('#tree').find('[data-role="node"]').each(function() {
  let expander = $(this).find('[data-role="expander"]');
  if (expander.attr('data-mode') === 'open') {
  savedState.push($(this).attr('data-id'));
}
});
}

  function restoreTreeState() {
  $('#tree').find('[data-role="node"]').each(function() {
    let node = $(this);
    let id = node.attr('data-id');

    if (savedState.includes(id)) {
      node.find('[data-role="expander"]').attr('data-mode', 'open').find('i.gj-icon').removeClass('chevron-right').addClass('chevron-down');
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
  convertNode(fileNode, treeData, 1); // 재귀적으로 노드를 변환합니다.
  return treeData;
}

  // FileNode 객체를 트리뷰 노드로 변환
  function convertNode(fileNode, treeData, nodeId) {
  var node = {
  id: nodeId,
  text: "<a href='" + fileNode.text + "'>" + fileNode.name + "</a>",
  flagUrl: fileNode.flagUrl,
  children: []
};

  fileNode.children.forEach(function (child) {
  convertNode(child, node.children, nodeId + 1);
  nodeId++;
});

  treeData.push(node);
}

  //html , css, jscode값을 분배하는 코드
  function setCodeValues(htmlCode, cssCode, jsCode) {
  const htmlCodeEl = document.querySelector("[data-html]");
  const cssCodeEl = document.querySelector("[data-css]");
  const jsCodeEl = document.querySelector("[data-js]");

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
  console.log(filename.split('.')[0]);
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
  console.log('CSS Code:', cssCode);
  console.log('HTML Code:', htmlCode);
  console.log('JavaScript Code:', jsCode);
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
  console.log(response); // 응답 출력
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

  // "직접입력" 값이 변경될 때 처리
  recustomExtension.addEventListener("input", function () {
  // 직접 입력 값을 select's value로 설정
  reextensionSelect.value = recustomEx.value;
  console.log("설렉트문 값:"+reextensionSelect.value);
  console.log("직접입력 값:"+recustomEx.value);
});


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
  })
});


<!--자동저장 기능-->

  function autosave () {
  let extension = document.getElementById("extension").value;
  let name =document.getElementById("filename").value; // 저장하고 싶은 파일의 파일명 값
  let filename = name + extension ;
  const htmlCodeEl = document.querySelector("[data-html]");
  const cssCodeEl = document.querySelector("[data-css]");
  const jsCodeEl = document.querySelector("[data-js]");
  const htmlCode = htmlCodeEl.value;
  const cssCode = cssCodeEl.value;
  const jsCode = jsCodeEl.value;
  const content = `<html>\n<head>\n<style>\n${cssCode}\n</style>\n</head>\n<body>\n${htmlCode}\n</body>\n<script>\n${jsCode}\n<\/script>\n</html>`;

  if (!isValidFilename(name)) {
  alert("파일명에는 특수 문자 및 일부 예약어를 사용할 수 없습니다.");
  return; // 추가 실행 중단
}
  let code = {'filename' : filename,'content' : content };

  axios.post("/editor/autoSave", code)
  .then((response) => {
  // 서버 응답이 성공한 경우
  showSuccessMessage();
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
  // localStorage.setItem('htmlContent', document.getElementById('htmlTextarea').value);
  // localStorage.setItem('cssContent', document.getElementById('cssTextarea').value);
  // localStorage.setItem('jsContent', document.getElementById('jsTextarea').value);
  localStorage.setItem('htmlContent', htmlEditor.value);
  localStorage.setItem('cssContent', cssEditor.value);
  localStorage.setItem('jsContent', jsEditor.value);
}

  // Load content from local storage on page load
  // document.getElementById('htmlTextarea').value = localStorage.getItem('htmlContent') || '';
  // document.getElementById('cssTextarea').value = localStorage.getItem('cssContent') || '';
  // document.getElementById('jsTextarea').value = localStorage.getItem('jsContent') || '';
  htmlEditor.value= localStorage.getItem('htmlContent') || '';
  cssEditor.value= localStorage.getItem('cssContent') || '';
  jsEditor.value= localStorage.getItem('jsContent') || '';

  document.addEventListener('DOMContentLoaded', function() {
  // HTML textarea
  var htmlTextarea = document.getElementById('htmlTextarea');
  var savedHtmlContent = localStorage.getItem('htmlContent');

  // CSS textarea
  var cssTextarea = document.getElementById('cssTextarea');
  var savedCssContent = localStorage.getItem('cssContent');

  // JS textarea
  var jsTextarea = document.getElementById('jsTextarea');
  var savedJsContent = localStorage.getItem('jsContent');

  // 코드가 어디에 어떻게 박히느지 몰라서 추가함..
  var htmlCodeEl2 = document.querySelector("[data-html]");
  var cssCodeEl2 = document.querySelector("[data-css]");
  var jsCodeEl2 = document.querySelector("[data-js]");
  var htmlCode2 = htmlCodeEl.value;
  var cssCode2 = cssCodeEl.value;
  var jsCode2 = jsCodeEl.value;

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
  htmlEditor.setValue(savedHtmlContent)
}

  if (savedCssContent !== null) {
  cssTextarea.value = savedCssContent;
  cssEditor.setValue(savedCssContent)
}

  if (savedJsContent !== null) {
  jsTextarea.value = savedJsContent;
  jsEditor.setValue(savedJsContent)
}
} else { // If the user clicks Cancel, clear the values in the textareas
  console.log('caches' in window)
  console.log(window.caches)

  htmlCode2.value = "";
  cssCode2.value = "";
  jsCode2.value = "";
  htmlTextarea.value="";
  cssTextarea.value="";
  jsTextarea.value="";
  htmlEditor.value="";
  cssEditor.value="";
  jsEditor.value="";
  $(".CodeMirror-line").text("");
  localStorage.removeItem('htmlContent');
  localStorage.removeItem('cssContent');
  localStorage.removeItem('jsContent');

}});

  const checkbox = document.getElementById("autoSaveCheck");
  let auto; // 변수 선언
  let local = setInterval(saveToLocalStorage, 4000);

  checkbox.addEventListener("change", function() {
  if (checkbox.checked) {
  clearInterval(auto);
  console.log("자동저장중");

  auto = setInterval(autosave,4000);
  localStorage.removeItem('htmlContent');
  localStorage.removeItem('cssContent');
  localStorage.removeItem('jsContent');
  clearInterval(local); // 변수를 정의한 후 clearInterval 호출
} else {
  clearInterval(auto);
  console.log("자동저장해제");
  local = setInterval(saveToLocalStorage, 4000);
}
});

  // 파일 업로드를 위한 이벤트 핸들러
  document.addEventListener('DOMContentLoaded', function () {
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
                const { cssCode, htmlCode, jsCode } = extractCodeBlocks(response.data.result);
                setCodeValues(htmlCode, cssCode, jsCode);
                document.getElementById('downloadName').value = response.data.fileName;

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
      })
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
  console.log(jsCode);
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


