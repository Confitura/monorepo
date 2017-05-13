import {Component, OnInit} from "@angular/core";
import {Observable} from "rxjs";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {Presentation} from "../shared/presentation.model";
import {PresentationService} from "../shared/presentation.service";
import {Tag} from "../shared/tag.model";
import {FormControl} from "@angular/forms";
@Component({
    templateUrl: "./presentation-edit.component.html"
})
export class PresentationEditComponent implements OnInit {

    model: Presentation = new Presentation();
    submitted: boolean = false;

    constructor(private service: PresentationService,
                private router: Router,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.route.params
            .subscribe((params: Params) => {
                let id = params["id"];
                if (id) {
                    this.service.getOne(id)
                        .flatMap(pres => this.service.getCospeakers(id)
                            .map(it => {
                            pres.cospeakers = it;
                            return pres;
                        }))
                        .subscribe((presentation: Presentation) => this.model = presentation);
                }
            })

    }

    save(form: FormControl) {
        this.submitted = true;
        if (form.valid) {
            this.service.save(this.model)
                .subscribe(() => this.router.navigate(["/profile"]));
        }
    }

    public tags = (): Observable<Tag[]> => {
        return this.service.allTags();
    }
}
