import {Component, OnInit} from "@angular/core";
import {Observable} from "rxjs";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {Presentation} from "../shared/presentation.model";
import {PresentationService} from "../shared/presentation.service";
import {Tag} from "../shared/tag.model";
@Component({
    templateUrl: "./presentation-edit.component.html"
})
export class PresentationEditComponent implements OnInit {

    model: Presentation = new Presentation();

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
                        .subscribe((presentation: Presentation) => this.model = presentation);
                }
            })

    }

    save() {
        this.service.save(this.model)
            .subscribe(() => this.router.navigate(["/profile"]));
    }

    public tags = (): Observable<Tag[]> => {
        return this.service.allTags();

    }
}