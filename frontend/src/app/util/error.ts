import { Modal } from 'angular2-modal/plugins/bootstrap';
import { ApiCallResult } from '../service';

export function showError(modal: Modal, result: ApiCallResult<void|{}>): void {
  console.dir(result);
  let validationErrors = result.error;

  let errorBody = '<ul>';
  validationErrors.map(e => {
    let baseStr = e.field + ': ';
    let errorStr = 'unknown';
    if (e.type === 'size') {
      errorStr = 'size (' + e.sizeInfo.min + ', ' + e.sizeInfo.max + ')';
    } else if (e.type === 'pattern') {
      errorStr = 'pattern ' + e.patternInfo.pattern;
    } else if (e.type === 'other') {
      errorStr = 'other ' + e.otherInfo.name;
    }
    return baseStr + errorStr;
  }).forEach(s => {
    errorBody = errorBody + '<li>' + s + '</li>';
  });
  errorBody = errorBody + '</ul>';

  modal.alert()
      .size('sm')
      .showClose(true)
      .title('Error')
      .body(errorBody)
      .open();
}
