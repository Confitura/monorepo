import {PartnerService} from "../shared/partner.service";
import {Component} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {Partner} from "../shared/partner.model";
import {FileUploader} from "ng2-file-upload";
import {CurrentUser} from "../../../security/current-user.service";
import {HttpConfiguration} from "../../../shared/http-configuration.service";
import * as Marked from "marked";
import {Location} from "@angular/common";
import "rxjs/add/operator/switchMap"

@Component({
    templateUrl: "./partner.component.html"
})
export class PartnerComponent {
    partner: Partner;
    uploader: FileUploader;

    constructor(private route: ActivatedRoute,
                private service: PartnerService,
                private currentUser: CurrentUser,
                private config: HttpConfiguration,
                private location: Location) {
        this.uploader = new FileUploader({
            authToken: this.currentUser.getToken(),
            autoUpload: true
        });
        this.uploader.onCompleteAll = () => {
            this.service.getBy(this.partner.id)
                .subscribe((partner) => {
                    this.partner = partner;
                    this.partner.logo += `?v=${new Date().getMilliseconds()}`
                })

        };


        this.route.params
            .switchMap((params) => this.service.getBy(params["id"]))
            .subscribe((partner) => {
                this.partner = partner;
                $(window).scrollTop(0);
                const url = `${config.apiServer}/resources/partners/${partner.id}`;
                this.uploader.options.url = url;

            });

    }

    renderDescription(): string {
        return Marked(this.partner.description);
    }


    uploadLogo() {
        $("input[type=file]").click();
    }

    delete() {
        this.service.delete(this.partner)
            .subscribe((response) => this.location.back());
    }
}