import { Modal, } from 'angular2-modal/plugins/bootstrap';
import { ApiCallResult } from '../service';
import { ValidationErrorJson } from '../api/model/ValidationErrorJson';

export function showValidationError(modal: Modal, result: ApiCallResult<void | {}>): void {
  console.dir(result);
  let validationErrors = result.error;

  let errorBody = '<ul>';
  validationErrors.map(e => {
    return mapError(e);
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

function mapError(error: ValidationErrorJson): String {
  var errorMsg = '';
  if (error.type === 'size') {
    errorMsg = 'size (' + error.sizeInfo.min + ', ' + error.sizeInfo.max + ')';
  } else if (error.type === 'pattern') {
    errorMsg = 'pattern ' + error.patternInfo.pattern;
  } else if (error.type === 'other') {
    if (error.field.includes('.')) {
      error.field = error.field.split('.')[1];
    }
    switch (error.otherInfo.name) {
      case 'inuse': {
        errorMsg = 'The value for the field "' + error.field + '" is already in use. Please enter another value!';
        break;
      }
      case 'notpermitted': {
        errorMsg = 'You are not permitted to use the selected value for the field "' + error.field + '". Please enter another value!';
        break;
      }
      case 'maxsize': {
        errorMsg = 'The uploaded file for the field "' + error.field + '" is too big. Please choose another file!';
        break;
      }
      case 'passwordincorrect': {
        errorMsg = 'The password is not correct. Please try again!';
        break;
      }
      case 'invalidValue': {
        errorMsg = 'The value for the field "' + error.field + '" is not valid. Please enter another value!';
        break;
      }
      case 'invalidFormat': {
        errorMsg = 'The format for the field "' + error.field + '" is not valid. Please enter another value!';
        break;
      }
       case 'tagDisabled': {
        errorMsg = 'The seleced Tag is disabled. Please select another tag!';
        break;
      }
      default: {
        errorMsg = 'other ' + error.otherInfo.name;
      }
    }
  }
  return errorMsg;
}
