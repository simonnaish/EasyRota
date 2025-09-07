import {NgIf} from "@angular/common";
import {Component, inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {RouterLink} from "@angular/router";
import {TranslatePipe} from "@ngx-translate/core";
import {Subscription} from "rxjs";
import {AuthService} from "src/app/services/auth.service";

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [
        MatButton,
        TranslatePipe,
        RouterLink,
        NgIf
    ],
    templateUrl: './home.component.html',
    styleUrl: './home.component.scss'
})
export class HomeComponent {

    private readonly _authService: AuthService = inject(AuthService);

    public get authenticated(): boolean {
        return this._authService.isAuthenticated();
    }

    public logout(): void {
        let subscription: Subscription;
        subscription = this._authService.logout().subscribe({
            next: (response) => console.log(response),
            error: (e) => {
                console.log(e);
                subscription.unsubscribe();
            },
            complete: () => subscription.unsubscribe()
        });
    }

}
