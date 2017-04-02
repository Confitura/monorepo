import {Component, OnInit} from "@angular/core";
import {Presentation} from "./presentation.model";
import {PresentationService} from "./presentation.service";
import {Tag} from "./tag.model";
import {Observable} from "rxjs";
import {Router} from "@angular/router";
@Component({
    templateUrl: "./presentation.component.html"
})
export class PresentationComponent implements OnInit {

    model: Presentation = new Presentation();

    constructor(private service: PresentationService, private router: Router) {
    }

    ngOnInit(): void {
    }

    save() {
        this.service.save(this.model).subscribe(() => this.router.navigate(["/my-profile"]));
    }

    public tags = (): Observable<Tag[]> => {
        return this.service.allTags();

    }
}