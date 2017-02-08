import {Component, OnInit} from "@angular/core";
import {PartnerService} from "../shared/partner.service";
import {Partner} from "../shared/partner.model";
import "./partners.scss";
@Component({
    templateUrl: "./partners.component.html"
})
export class PartnersComponent implements OnInit {
    types = ["dupa", "platinum", "gold", "silver", "bronze", "media", "technical"];
    private partners: Map<String, Partner[]>;


    constructor(private service: PartnerService) {
    }

    ngOnInit(): void {
        this.service.getAll()
            .subscribe((partners) => this.partners = partners)
        ;


    }
}
