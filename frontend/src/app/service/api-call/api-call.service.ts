import {Injectable} from '@angular/core';
import {Response, Headers} from '@angular/http';
import {Observable} from 'rxjs';
import {AuthService} from '..';

export type ApiCallFn = (extraParams: any) => Observable<Response>;

export class ApiCallError {
}

export class ApiCallResult<T> {
  constructor(
    readonly apiCall: ApiCallFn,
    readonly extraHeaders: {[name: string]: string} | null,
    readonly value: T|ApiCallError
  ) {}

  get isValid(): boolean { return !(this.value instanceof ApiCallError); }

  get error(): ApiCallError {
    if (this.isValid) {
      throw new Error('Called error on valid ApiCallResult.');
    } else {
      return this.value as ApiCallError;
    }
  }

  get result(): T {
    if (this.isValid) {
      return this.value as T;
    } else {
      throw new Error('Called result on invalid ApiCallResult.');
    }
  }
}

@Injectable()
export class ApiCallService {
  constructor(
    private readonly authService: AuthService
  ) {}

  callNoError<T>(apiCall: ApiCallFn, extraHeaders?: {[name: string]: string}): Observable<T> {
    return this
      .call<T>(apiCall, extraHeaders)
      .map(res => {
        if (res.isValid) {
          return res.result;
        } else {
          // TODO global error
          throw new Error('TODO nice error handling yada yada');
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

    return apiCall({headers: headers}).map(resp => {
      // TODO global error handling
      return new ApiCallResult<T>(apiCall, extraHeaders || null, resp.json());
    });
  }
}
