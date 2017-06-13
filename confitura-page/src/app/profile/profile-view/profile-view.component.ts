import {Component, OnInit} from "@angular/core";
import {Observable} from "rxjs";
import {User} from "../../pages/profile/user.model";
import {UserService} from "../../pages/profile/user.service";
import {CurrentUser} from "../../security/current-user.service";
import {Presentation} from "../shared/presentation.model";
import {PresentationService} from "../shared/presentation.service";
import {ActivatedRoute, Params} from "@angular/router";
@Component({
    templateUrl: "./profile-view.component.html"
})
export class ProfileViewComponent implements OnInit {

    model: User;
    presentations: Observable<Presentation[]>;

    constructor(private service: UserService,
                private presentationService: PresentationService,
                private route: ActivatedRoute) {

    }

    ngOnInit(): void {
        this.reload();
    }

    reload() {
        this.route.params
            .subscribe((params: Params) => {
                let id = params["id"];
                if (id) {
                    this.service.getBy(id)
                        .subscribe(user => {
                            this.model = user;
                            user.photo += "?v=" + new Date().getMilliseconds();
                        });
                    this.presentations = this.presentationService.getAllFor(id);
                }
            });
    }

    remove(presentation: Presentation) {
        this.presentationService.remove(presentation)
            .subscribe(() => this.reload());
    }


}