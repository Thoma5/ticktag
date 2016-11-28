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
export class TicketApi {
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
     * createTicket
     * 
     * @param req req
     */
    public createTicketUsingPOST(req: models.CreateTicketRequestJson, extraHttpRequestParams?: any): Observable<models.TicketResultJson> {
        return this.createTicketUsingPOSTWithHttpInfo(req, extraHttpRequestParams)
            .map((response: Response) => {
                if (response.status === 204) {
                    return undefined;
                } else {
                    return response.json();
                }
            });
    }

    /**
     * deleteTicket
     * 
     * @param id id
     */
    public deleteTicketUsingDELETE(id: string, extraHttpRequestParams?: any): Observable<{}> {
        return this.deleteTicketUsingDELETEWithHttpInfo(id, extraHttpRequestParams)
            .map((response: Response) => {
                if (response.status === 204) {
                    return undefined;
                } else {
                    return response.json();
                }
            });
    }

    /**
     * getTicketProgress
     * 
     * @param id id
     */
    public getTicketProgressUsingGET(id: string, extraHttpRequestParams?: any): Observable<models.TicketProgressResultJson> {
        return this.getTicketProgressUsingGETWithHttpInfo(id, extraHttpRequestParams)
            .map((response: Response) => {
                if (response.status === 204) {
                    return undefined;
                } else {
                    return response.json();
                }
            });
    }

    /**
     * getTicket
     * 
     * @param id id
     */
    public getTicketUsingGET(id: string, extraHttpRequestParams?: any): Observable<models.TicketResultJson> {
        return this.getTicketUsingGETWithHttpInfo(id, extraHttpRequestParams)
            .map((response: Response) => {
                if (response.status === 204) {
                    return undefined;
                } else {
                    return response.json();
                }
            });
    }

    /**
     * listTicketsFuzzy
     * 
     * @param projectId projectId
     * @param q q
     * @param order order
     */
    public listTicketsFuzzyUsingGET(projectId: string, q: string, order: Array<string>, extraHttpRequestParams?: any): Observable<Array<models.TicketResultJson>> {
        return this.listTicketsFuzzyUsingGETWithHttpInfo(projectId, q, order, extraHttpRequestParams)
            .map((response: Response) => {
                if (response.status === 204) {
                    return undefined;
                } else {
                    return response.json();
                }
            });
    }

    /**
     * listTickets
     * 
     * @param projectId projectId
     * @param order order
     * @param page page
     * @param size size
     */
    public listTicketsUsingGET(projectId: string, order: Array<string>, page?: number, size?: number, extraHttpRequestParams?: any): Observable<models.PageTicketResultJson> {
        return this.listTicketsUsingGETWithHttpInfo(projectId, order, page, size, extraHttpRequestParams)
            .map((response: Response) => {
                if (response.status === 204) {
                    return undefined;
                } else {
                    return response.json();
                }
            });
    }

    /**
     * updateTicket
     * 
     * @param req req
     * @param id id
     */
    public updateTicketUsingPUT(req: models.UpdateTicketRequestJson, id: string, extraHttpRequestParams?: any): Observable<models.TicketResultJson> {
        return this.updateTicketUsingPUTWithHttpInfo(req, id, extraHttpRequestParams)
            .map((response: Response) => {
                if (response.status === 204) {
                    return undefined;
                } else {
                    return response.json();
                }
            });
    }


    /**
     * createTicket
     * 
     * @param req req
     */
    public createTicketUsingPOSTWithHttpInfo(req: models.CreateTicketRequestJson, extraHttpRequestParams?: any): Observable<Response> {
        const path = this.basePath + `/ticket`;

        let queryParameters = new URLSearchParams();
        let headers = new Headers(this.defaultHeaders.toJSON()); // https://github.com/angular/angular/issues/6845
        // verify required parameter 'req' is not null or undefined
        if (req === null || req === undefined) {
            throw new Error('Required parameter req was null or undefined when calling createTicketUsingPOST.');
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
     * deleteTicket
     * 
     * @param id id
     */
    public deleteTicketUsingDELETEWithHttpInfo(id: string, extraHttpRequestParams?: any): Observable<Response> {
        const path = this.basePath + `/ticket/${id}`;

        let queryParameters = new URLSearchParams();
        let headers = new Headers(this.defaultHeaders.toJSON()); // https://github.com/angular/angular/issues/6845
        // verify required parameter 'id' is not null or undefined
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling deleteTicketUsingDELETE.');
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
     * getTicketProgress
     * 
     * @param id id
     */
    public getTicketProgressUsingGETWithHttpInfo(id: string, extraHttpRequestParams?: any): Observable<Response> {
        const path = this.basePath + `/ticket/${id}/progress`;

        let queryParameters = new URLSearchParams();
        let headers = new Headers(this.defaultHeaders.toJSON()); // https://github.com/angular/angular/issues/6845
        // verify required parameter 'id' is not null or undefined
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling getTicketProgressUsingGET.');
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
     * getTicket
     * 
     * @param id id
     */
    public getTicketUsingGETWithHttpInfo(id: string, extraHttpRequestParams?: any): Observable<Response> {
        const path = this.basePath + `/ticket/${id}`;

        let queryParameters = new URLSearchParams();
        let headers = new Headers(this.defaultHeaders.toJSON()); // https://github.com/angular/angular/issues/6845
        // verify required parameter 'id' is not null or undefined
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling getTicketUsingGET.');
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
     * listTicketsFuzzy
     * 
     * @param projectId projectId
     * @param q q
     * @param order order
     */
    public listTicketsFuzzyUsingGETWithHttpInfo(projectId: string, q: string, order: Array<string>, extraHttpRequestParams?: any): Observable<Response> {
        const path = this.basePath + `/ticket/fuzzy`;

        let queryParameters = new URLSearchParams();
        let headers = new Headers(this.defaultHeaders.toJSON()); // https://github.com/angular/angular/issues/6845
        // verify required parameter 'projectId' is not null or undefined
        if (projectId === null || projectId === undefined) {
            throw new Error('Required parameter projectId was null or undefined when calling listTicketsFuzzyUsingGET.');
        }
        // verify required parameter 'q' is not null or undefined
        if (q === null || q === undefined) {
            throw new Error('Required parameter q was null or undefined when calling listTicketsFuzzyUsingGET.');
        }
        // verify required parameter 'order' is not null or undefined
        if (order === null || order === undefined) {
            throw new Error('Required parameter order was null or undefined when calling listTicketsFuzzyUsingGET.');
        }
        if (projectId !== undefined) {
            queryParameters.set('projectId', <any>projectId);
        }
        if (q !== undefined) {
            queryParameters.set('q', <any>q);
        }
        if (order !== undefined) {
            queryParameters.set('order', <any>order);
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
     * listTickets
     * 
     * @param projectId projectId
     * @param order order
     * @param page page
     * @param size size
     */
    public listTicketsUsingGETWithHttpInfo(projectId: string, order: Array<string>, page?: number, size?: number, extraHttpRequestParams?: any): Observable<Response> {
        const path = this.basePath + `/ticket`;

        let queryParameters = new URLSearchParams();
        let headers = new Headers(this.defaultHeaders.toJSON()); // https://github.com/angular/angular/issues/6845
        // verify required parameter 'projectId' is not null or undefined
        if (projectId === null || projectId === undefined) {
            throw new Error('Required parameter projectId was null or undefined when calling listTicketsUsingGET.');
        }
        // verify required parameter 'order' is not null or undefined
        if (order === null || order === undefined) {
            throw new Error('Required parameter order was null or undefined when calling listTicketsUsingGET.');
        }
        if (projectId !== undefined) {
            queryParameters.set('projectId', <any>projectId);
        }
        if (page !== undefined) {
            queryParameters.set('page', <any>page);
        }
        if (size !== undefined) {
            queryParameters.set('size', <any>size);
        }
        if (order !== undefined) {
            queryParameters.set('order', <any>order);
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
     * updateTicket
     * 
     * @param req req
     * @param id id
     */
    public updateTicketUsingPUTWithHttpInfo(req: models.UpdateTicketRequestJson, id: string, extraHttpRequestParams?: any): Observable<Response> {
        const path = this.basePath + `/ticket/${id}`;

        let queryParameters = new URLSearchParams();
        let headers = new Headers(this.defaultHeaders.toJSON()); // https://github.com/angular/angular/issues/6845
        // verify required parameter 'req' is not null or undefined
        if (req === null || req === undefined) {
            throw new Error('Required parameter req was null or undefined when calling updateTicketUsingPUT.');
        }
        // verify required parameter 'id' is not null or undefined
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling updateTicketUsingPUT.');
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

