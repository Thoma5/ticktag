import {Injectable} from '@angular/core';
import {Response, Headers} from '@angular/http';
import {Observable} from 'rxjs';
import {AuthService} from '..';
import {ValidationErrorJson} from '../../api';

// Do not expose this function, it is dangerous if called on objects that are
// not meant to be frozen this way!
function deepFreeze(object: any) {
  if (object instanceof Object) {
    for (let key in object) {
      if (object.hasOwnProperty(key)) {
        deepFreeze(key);
      }
    }
    Object.freeze(object);
  }
}

export abstract class ErrorHandler {
  abstract onError(resp: any): void;
}

export type ApiCallFn = (extraParams: any) => Observable<Response>;

export class ApiCallResult<T> {
  // TODO move this to ctr once Intellij supports readonly properties in ctr
  readonly apiCall: ApiCallFn;
  readonly extraHeaders: {[name: string]: string} | null;
  readonly isValid: boolean;
  private readonly value: T|ValidationErrorJson[];

  constructor(apiCall: ApiCallFn, extraHeaders: {[name: string]: string}, isValid: boolean,
              value: T|ValidationErrorJson[]) {
    this.apiCall = apiCall;
    this.extraHeaders = extraHeaders;
    this.isValid = isValid;
    this.value = value;
  }

  get error(): ValidationErrorJson[] {
    if (this.isValid) {
      throw new Error('Called error on valid ApiCallResult.');
    } else {
      return <ValidationErrorJson[]>this.value;
    }
  }

  get result(): T {
    if (this.isValid) {
      return <T>this.value;
    } else {
      throw new Error('Called result on invalid ApiCallResult.');
    }
  }
}

@Injectable()
export class ApiCallService {
  private errorHandler: ErrorHandler | null = null;

  constructor(private authService: AuthService) {
  }

  initErrorHandler(errorHandler: ErrorHandler): void {
    if (this.errorHandler != null) {
      throw new Error('Error handler is already registered');
    }
    this.errorHandler = errorHandler;
  }

  callNoError<T>(apiCall: ApiCallFn, extraHeaders?: {[name: string]: string}): Observable<T> {
    return this
      .call<T>(apiCall, extraHeaders)
      .flatMap(res => {
        if (res.isValid) {
          return Observable.of(res.result);
        } else {
          this.handleError(res);
          return Observable.throw(res);
        }
      });
  }

  call<T>(apiCall: ApiCallFn, extraHeaders?: {[name: string]: string}): Observable<ApiCallResult<T>> {
    let headers = new Headers({
      'Accept': 'application/json',
      'Content-Type': 'application/json',
    });
    if (this.authService.user) {
      headers.append('X-Authorization', this.authService.user.token);
    }
    if (extraHeaders) {
      for (let headerName in extraHeaders) {
        if (extraHeaders.hasOwnProperty(headerName)) {
          headers.append(headerName, extraHeaders[headerName]);
        }
      }
    }

    return apiCall({headers: headers})
      .map(resp => {
        let data = (resp.text().length === 0) ? null : resp.json();
        if (data) {
          deepFreeze(data);
        }
        return new ApiCallResult<T>(apiCall, extraHeaders || null, true, data);
      })
      .catch(resp => {
        if (resp instanceof Response) {
          if (resp.status === 422) {
            return Observable.of(new ApiCallResult<T>(apiCall, extraHeaders || null, false, resp.json()));
          } else {
            this.handleError(resp);
            return Observable.throw(resp);
          }
        } else {
          this.handleError(resp);
          return Observable.throw(resp);
        }
      });
  }

  private handleError(resp: any) {
    if (this.errorHandler != null) {
      this.errorHandler.onError(resp);
    } else {
      throw new Error('Unhandled error: ' + resp);
    }
  }
}
