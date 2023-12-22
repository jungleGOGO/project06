const htmlCodeEl = document.querySelector("[data-html]");
const cssCodeEl = document.querySelector("[data-css]");
const jsCodeEl = document.querySelector("[data-js]");
const codeEl = document.querySelector("[data-code]").contentWindow.document;
const runButtonEl = document.querySelector("#run");
const clearButtonEl = document.querySelector("#clear");
const clearHtmlButtonEl = document.querySelector("#clearHtml");
const clearCssButtonEl = document.querySelector("#clearCss");
const clearJsButtonEl = document.querySelector("#clearJs");
const clearResult = document.querySelector("#clearRs");

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
  codeEl.open();
  codeEl.innerHTML = "";
  codeEl.close();
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
