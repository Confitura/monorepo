import {Component, OnInit} from "@angular/core";
import {ParticipantService} from "./participant.service";
import {Participant} from "./participant.model";
import {FileUploader} from "ng2-file-upload";
import {HttpConfiguration} from "../../shared/http-configuration.service";
import {CurrentUser} from "../../security/current-user.service";
import {ConfirmationService} from "../../shared/confirmation.service";
import AlertType = SweetAlert.AlertType;
@Component({
    templateUrl: "./participant-list.component.html"
})
export class ParticipantListComponent implements OnInit {


    list: Participant[];
    uploader: FileUploader;

    constructor(private  service: ParticipantService,
                private config: HttpConfiguration,
                private user: CurrentUser,
                private confirmation: ConfirmationService) {
        this.uploader = new FileUploader({
            authToken: this.user.getToken(),
            url: `${config.apiServer}/participants/upload`,
            autoUpload: true,
        });

        this.uploader.onCompleteAll = () => this.ngOnInit();
    }

    ngOnInit(): void {

        this.service.getAll()
            .subscribe(list => {
                this.list = list;
                setTimeout(() => $("#participants").DataTable({deferRender: true}));

            });

    }

    upload() {
        $("input[type=file]").click();
    }

    sendReminder() {
        this.confirmation.show("you want to send reminder emails to all unregistered participants?")
            .then(() => this.service.sendReminder().subscribe());
    }

    sendTickets() {
        this.confirmation.show("you want to send tickets to participants which hasn't recieved one?")
            .then(() => this.service.sendTickets().subscribe());
    }

    sendSurveys() {
        this.confirmation.show("you want to send surveys to all attendees?")
            .then(() => this.service.sendSurveys().subscribe());
    }
}
