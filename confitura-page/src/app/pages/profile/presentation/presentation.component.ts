import {Component, OnInit} from "@angular/core";
import {Presentation} from "./presentation.model";
import {PresentationService} from "./presentation.service";
import {Tag} from "./tag.model";
import {Observable} from "rxjs";
import {ActivatedRoute, Params, Router} from "@angular/router";
@Component({
    templateUrl: "./presentation.component.html"
})
export class PresentationComponent implements OnInit {

    model: Presentation = new Presentation();

    constructor(private service: PresentationService,
                private router: Router,
                private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.route.params
            .subscribe((params: Params) => {
                console.log(params);
                let id = params["id"];
                if (id) {
                    this.service.getOne(id)
                        .subscribe((presentation: Presentation) => this.model = presentation);
                }
            })

    }

    save() {
        this.service.save(this.model).subscribe(() => this.router.navigate(["/my-profile"]));
    }

    public tags = (): Observable<Tag[]> => {
        return this.service.allTags();

    }
}