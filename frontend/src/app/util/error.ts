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
  let errorMsg = '';
  if (error.field.includes('.')) {
    error.field = error.field.split('.')[1];
  }

  if (error.type === 'size') {
    errorMsg = error.field + ' size must be between ' + error.sizeInfo.min + ' and ' + error.sizeInfo.max;
  } else if (error.type === 'pattern') {
    errorMsg = error.field + ' must match pattern ' + error.patternInfo.pattern;
  } else if (error.type === 'other') {
    if (error.field === 'commands') {
      errorMsg = 'Invalid commands';
    } else if (error.field === 'mentionedTicketNumbers') {
      errorMsg = 'Mentioned ticked was invalid: ' + error.otherInfo.name;
    } else {
      switch (error.otherInfo.name.toLowerCase()) {
        case 'inuse':
          errorMsg = 'The value for the field "' + error.field + '" is already in use. Please enter another value!';
          break;

        case 'notpermitted':
          errorMsg = 'You are not permitted to use the selected value for the field "' + error.field + '". Please enter another value!';
          break;

        case 'maxsize':
          errorMsg = 'The uploaded file for the field "' + error.field + '" is too big. Please choose another file!';
          break;

        case 'passwordincorrect':
          errorMsg = 'The password is not correct. Please try again!';
          break;

        case 'invalidvalue':
          errorMsg = 'The value for the field "' + error.field + '" is not valid. Please enter another value!';
          break;

        case 'invalidformat':
          errorMsg = 'The format for the field "' + error.field + '" is not valid. Please enter another value!';
          break;

        case 'tagdisabled':
          errorMsg = 'The selected tag is disabled. Please select another tag!';
          break;

        case 'nonestedsubtickets':
          errorMsg = 'Only one layer of subtickets is allowed';
          break;

        case 'notallowed':
          errorMsg = 'This change is not permitted';
          break;

        default:
          errorMsg = 'other ' + error.otherInfo.name;
      }
    }
  }
  return errorMsg;
}
