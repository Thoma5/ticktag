/* tslint:disable */
/**
 * TickTag REST API
 * TickTag issue tracking API
 *
 * OpenAPI spec version: 1.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Inject, Injectable, Optional }                      from '@angular/core';
import { Http, Headers, URLSearchParams }                    from '@angular/http';
import { RequestMethod, RequestOptions, RequestOptionsArgs } from '@angular/http';
import { Response, ResponseContentType }                     from '@angular/http';

import { Observable }                                        from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import * as models                                           from '../model/models';
import { BASE_PATH }                                         from '../variables';
import { Configuration }                                     from '../configuration';

/* tslint:disable:no-unused-variable member-ordering */


@Injectable()
export class ProjectApi {
    protected basePath = 'http://localhost:8080/';
    public defaultHeaders: Headers = new Headers();
    public configuration: Configuration = new Configuration();

    constructor(protected http: Http, @Optional()@Inject(BASE_PATH) basePath: string, @Optional() configuration: Configuration) {
        if (basePath) {
            this.basePath = basePath;
        }
        if (configuration) {
            this.configuration = configuration;
        }
    }
	
	/**
     * 
     * Extends object by coping non-existing properties.
     * @param objA object to be extended
     * @param objB source object
     */
    private extendObj<T1,T2>(objA: T1, objB: T2) {
        for(let key in objB){
            if(objB.hasOwnProperty(key)){
                (<any>objA)[key] = (<any>objB)[key];
            }
        }
        return <T1&T2>objA;
    }

    /**
     * create
     * 
     * @param req req
     */
    public createUsingPOST1(req: models.CreateProjectRequestJson, extraHttpRequestParams?: any): Observable<models.ProjectResultJson> {
        return this.createUsingPOST1WithHttpInfo(req, extraHttpRequestParams)
            .map((response: Response) => {
                if (response.status === 204) {
                    return undefined;
                } else {
                    return response.json();
                }
            });
    }

    /**
     * delete
     * 
     * @param id id
     */
    public deleteUsingDELETE1(id: string, extraHttpRequestParams?: any): Observable<{}> {
        return this.deleteUsingDELETE1WithHttpInfo(id, extraHttpRequestParams)
            .map((response: Response) => {
                if (response.status === 204) {
                    return undefined;
                } else {
                    return response.json();
                }
            });
    }

    /**
     * list
     * 
     * @param page page
     * @param size size
     * @param order order
     * @param asc asc
     * @param name name
     * @param all all
     */
    public listUsingGET(page?: number, size?: number, order?: string, asc?: boolean, name?: string, all?: boolean, extraHttpRequestParams?: any): Observable<Array<models.ProjectResultJson>> {
        return this.listUsingGETWithHttpInfo(page, size, order, asc, name, all, extraHttpRequestParams)
            .map((response: Response) => {
                if (response.status === 204) {
                    return undefined;
                } else {
                    return response.json();
                }
            });
    }

    /**
     * update
     * 
     * @param id id
     * @param req req
     */
    public updateUsingPUT1(id: string, req: models.UpdateProjectRequestJson, extraHttpRequestParams?: any): Observable<models.ProjectResultJson> {
        return this.updateUsingPUT1WithHttpInfo(id, req, extraHttpRequestParams)
            .map((response: Response) => {
                if (response.status === 204) {
                    return undefined;
                } else {
                    return response.json();
                }
            });
    }


    /**
     * create
     * 
     * @param req req
     */
    public createUsingPOST1WithHttpInfo(req: models.CreateProjectRequestJson, extraHttpRequestParams?: any): Observable<Response> {
        const path = this.basePath + `/project`;

        let queryParameters = new URLSearchParams();
        let headers = new Headers(this.defaultHeaders.toJSON()); // https://github.com/angular/angular/issues/6845
        // verify required parameter 'req' is not null or undefined
        if (req === null || req === undefined) {
            throw new Error('Required parameter req was null or undefined when calling createUsingPOST1.');
        }


        // to determine the Content-Type header
        let consumes: string[] = [
            'application/json'
        ];

        // to determine the Accept header
        let produces: string[] = [
            'application/json'
        ];
        
            

        headers.set('Content-Type', 'application/json');


        let requestOptions: RequestOptionsArgs = new RequestOptions({
            method: RequestMethod.Post,
            headers: headers,
            body: req == null ? '' : JSON.stringify(req), // https://github.com/angular/angular/issues/10612
            search: queryParameters
        });
        
        // https://github.com/swagger-api/swagger-codegen/issues/4037
        if (extraHttpRequestParams) {
            requestOptions = this.extendObj(requestOptions, extraHttpRequestParams);
        }

        return this.http.request(path, requestOptions);
    }

    /**
     * delete
     * 
     * @param id id
     */
    public deleteUsingDELETE1WithHttpInfo(id: string, extraHttpRequestParams?: any): Observable<Response> {
        const path = this.basePath + `/project/${id}`;

        let queryParameters = new URLSearchParams();
        let headers = new Headers(this.defaultHeaders.toJSON()); // https://github.com/angular/angular/issues/6845
        // verify required parameter 'id' is not null or undefined
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling deleteUsingDELETE1.');
        }


        // to determine the Content-Type header
        let consumes: string[] = [
            'application/json'
        ];

        // to determine the Accept header
        let produces: string[] = [
            'application/json'
        ];
        
            



        let requestOptions: RequestOptionsArgs = new RequestOptions({
            method: RequestMethod.Delete,
            headers: headers,
            search: queryParameters
        });
        
        // https://github.com/swagger-api/swagger-codegen/issues/4037
        if (extraHttpRequestParams) {
            requestOptions = this.extendObj(requestOptions, extraHttpRequestParams);
        }

        return this.http.request(path, requestOptions);
    }

    /**
     * list
     * 
     * @param page page
     * @param size size
     * @param order order
     * @param asc asc
     * @param name name
     * @param all all
     */
    public listUsingGETWithHttpInfo(page?: number, size?: number, order?: string, asc?: boolean, name?: string, all?: boolean, extraHttpRequestParams?: any): Observable<Response> {
        const path = this.basePath + `/project`;

        let queryParameters = new URLSearchParams();
        let headers = new Headers(this.defaultHeaders.toJSON()); // https://github.com/angular/angular/issues/6845
        if (page !== undefined) {
            queryParameters.set('page', <any>page);
        }
        if (size !== undefined) {
            queryParameters.set('size', <any>size);
        }
        if (order !== undefined) {
            queryParameters.set('order', <any>order);
        }
        if (asc !== undefined) {
            queryParameters.set('asc', <any>asc);
        }
        if (name !== undefined) {
            queryParameters.set('name', <any>name);
        }
        if (all !== undefined) {
            queryParameters.set('all', <any>all);
        }


        // to determine the Content-Type header
        let consumes: string[] = [
            'application/json'
        ];

        // to determine the Accept header
        let produces: string[] = [
            'application/json'
        ];
        
            



        let requestOptions: RequestOptionsArgs = new RequestOptions({
            method: RequestMethod.Get,
            headers: headers,
            search: queryParameters
        });
        
        // https://github.com/swagger-api/swagger-codegen/issues/4037
        if (extraHttpRequestParams) {
            requestOptions = this.extendObj(requestOptions, extraHttpRequestParams);
        }

        return this.http.request(path, requestOptions);
    }

    /**
     * update
     * 
     * @param id id
     * @param req req
     */
    public updateUsingPUT1WithHttpInfo(id: string, req: models.UpdateProjectRequestJson, extraHttpRequestParams?: any): Observable<Response> {
        const path = this.basePath + `/project/${id}`;

        let queryParameters = new URLSearchParams();
        let headers = new Headers(this.defaultHeaders.toJSON()); // https://github.com/angular/angular/issues/6845
        // verify required parameter 'id' is not null or undefined
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling updateUsingPUT1.');
        }
        // verify required parameter 'req' is not null or undefined
        if (req === null || req === undefined) {
            throw new Error('Required parameter req was null or undefined when calling updateUsingPUT1.');
        }


        // to determine the Content-Type header
        let consumes: string[] = [
            'application/json'
        ];

        // to determine the Accept header
        let produces: string[] = [
            'application/json'
        ];
        
            

        headers.set('Content-Type', 'application/json');


        let requestOptions: RequestOptionsArgs = new RequestOptions({
            method: RequestMethod.Put,
            headers: headers,
            body: req == null ? '' : JSON.stringify(req), // https://github.com/angular/angular/issues/10612
            search: queryParameters
        });
        
        // https://github.com/swagger-api/swagger-codegen/issues/4037
        if (extraHttpRequestParams) {
            requestOptions = this.extendObj(requestOptions, extraHttpRequestParams);
        }

        return this.http.request(path, requestOptions);
    }

}
