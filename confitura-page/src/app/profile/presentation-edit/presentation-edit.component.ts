import {Component, OnInit} from "@angular/core";
import {Observable} from "rxjs";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {Presentation} from "../shared/presentation.model";
import {PresentationService} from "../shared/presentation.service";
import {Tag} from "../shared/tag.model";
import {FormControl} from "@angular/forms";
import {CurrentUser} from "../../security/current-user.service";
@Component({
    templateUrl: "./presentation-edit.component.html"
})
export class PresentationEditComponent implements OnInit {
    private userId: string = null;
    model: Presentation = new Presentation();
    submitted: boolean = false;

    constructor(private service: PresentationService,
                private user: CurrentUser,
                private router: Router,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.route.params
            .subscribe((params: Params) => {
                this.userId = params["userId"];
                if (this.userId == null) {
                    this.userId = this.user.get().jti;
                }
                let id = params["id"];
                if (id) {
                    this.service.getOne(id)
                        .subscribe((presentation: Presentation) => this.model = presentation);
                }
            })

    }

    save(form: FormControl) {
        this.submitted = true;
        if (form.valid) {
            this.service.save(this.userId, this.model)
                .subscribe(() => this.router.navigate(["/profile"]));
        }
    }

    public tags = (): Observable<Tag[]> => {
        return this.service.allTags();
    }
}