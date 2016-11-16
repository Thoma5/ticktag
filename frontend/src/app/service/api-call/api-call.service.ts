import {Injectable} from '@angular/core';
import {Response, Headers} from '@angular/http';
import {Observable} from 'rxjs';
import {AuthService} from '..';
import {ValidationErrorJson} from '../../api';

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
  constructor(private authService: AuthService) {
  }

  callNoError<T>(apiCall: ApiCallFn, extraHeaders?: {[name: string]: string}): Observable<T> {
    return this
      .call<T>(apiCall, extraHeaders)
      .map(res => {
        if (res.isValid) {
          return res.result;
        } else {
          throw res;
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
        return new ApiCallResult<T>(apiCall, extraHeaders || null, true, resp.json());
      })
      .catch(resp => {
        if (resp instanceof Response) {
          if (resp.status === 422) {
            return Observable.of(new ApiCallResult<T>(apiCall, extraHeaders || null, false, resp.json()));
          } else {
            throw resp;
          }
        } else {
          throw resp;
        }
      });
  }
}
