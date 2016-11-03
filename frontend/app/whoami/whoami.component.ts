import {Component, OnInit} from "@angular/core";
import {AuthApi} from "../api/api/AuthApi";
import {WhoamiResult} from "../api/model/WhoamiResult";
import {Headers} from "@angular/http";

@Component({
    moduleId: module.id,
    selector: 'whoami',
    templateUrl: 'whoami.component.html',
})
export class WhoamiComponent implements OnInit {
    private token:string;
    private me:WhoamiResult;

    constructor(private authApi:AuthApi) {
    }

    ngOnInit():void {
    }

    getWhoami():void {
        let headers = new Headers();
        headers.append('X-Authorization', this.token);

        this.authApi.whoamiUsingGET({'headers': headers})
            .subscribe(res => this.me = res, err => alert(err));
    }
}
