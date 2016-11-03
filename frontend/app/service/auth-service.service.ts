import {Injectable} from "@angular/core";
import {LocalStorageService} from "ng2-webstorage";

const KEY_TOKEN = "token";

@Injectable()
export class AuthService {
    constructor(private localStorageService:LocalStorageService) {
    }

    getToken():string|null {
        return this.localStorageService.retrieve(KEY_TOKEN) as string;
    }

    setToken(token: string|null):void {
        this.localStorageService.store(KEY_TOKEN, token);
    }
}
