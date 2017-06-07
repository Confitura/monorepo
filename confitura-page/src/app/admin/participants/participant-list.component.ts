import {Component, OnInit} from "@angular/core";
import {ParticipantService} from "./participant.service";
import {Observable} from "rxjs/Observable";
import {Participant} from "./participant.model";
import {FileUploader} from "ng2-file-upload";
import {HttpConfiguration} from "../../shared/http-configuration.service";
import {CurrentUser} from "../../security/current-user.service";
@Component({
    templateUrl: "./participant-list.component.html"
})
export class ParticipantListComponent implements OnInit {


    list: Observable<Participant[]>;
    private uploader: FileUploader;

    constructor(private  service: ParticipantService, private config: HttpConfiguration, private user: CurrentUser) {
        this.uploader = new FileUploader({
            authToken: this.user.getToken(),
            url: `${config.apiServer}/participants/upload`,
            autoUpload: true,
        });

        this.uploader.onCompleteAll = () => this.ngOnInit();
    }

    ngOnInit(): void {
        this.list = this.service.getAll();
    }

    upload() {
        $("input[type=file]").click();
    }
}