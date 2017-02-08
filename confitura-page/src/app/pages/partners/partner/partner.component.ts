import {PartnerService} from "../shared/partner.service";
import {Component} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {Partner} from "../shared/partner.model";
@Component({
    templateUrl: "./partner.component.html"
})
export class PartnerComponent {
    private partner: Partner;


    constructor(private route: ActivatedRoute, private service: PartnerService) {
        this.route.params
            .switchMap((params) => this.service.getBy(params["id"]))
            .subscribe((partner) => {
                this.partner = partner;
                $(window).scrollTop(0);
            });
    }
}