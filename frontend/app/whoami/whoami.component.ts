import {Component, OnInit} from "@angular/core";
import {AuthApi} from "../api/api/AuthApi";
import {WhoamiResult} from "../api/model/WhoamiResult";

@Component({
    moduleId: module.id,
    selector: 'whoami',
    templateUrl: 'whoami.component.html',
})
export class WhoamiComponent implements OnInit{
    private me:WhoamiResult;

    constructor(private authApi:AuthApi) {
    }

    ngOnInit():void {
        this.authApi.whoamiUsingGET()
            .subscribe(res => this.me = res, err => alert(err));
    }
}
