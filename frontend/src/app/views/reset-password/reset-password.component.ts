import {NgIf} from "@angular/common";
import {Component, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {ActivatedRoute, RouterLink} from "@angular/router";
import {TranslatePipe} from "@ngx-translate/core";
import {Subscription} from "rxjs";
import {AuthService} from "src/app/services/auth.service";

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [
    TranslatePipe,
    MatButton,
    RouterLink,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    NgIf
  ],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.scss'
})
export class ResetPasswordComponent implements OnInit {
  private readonly _fb: FormBuilder = inject(FormBuilder);
  private readonly _authService: AuthService = inject(AuthService);
  private readonly _route: ActivatedRoute = inject(ActivatedRoute);
  public resetPasswordForm: FormGroup | undefined;

  public token: string | undefined;


  public ngOnInit(): void {
    this.token = this._route.snapshot.queryParamMap.get('token') ?? '';
    this.initResetPasswordForm();
  }

  private initResetPasswordForm(): void {
    this.resetPasswordForm = this._fb.group({
      'password': ['12345', [Validators.minLength(5)]],
      'confirmPassword': ['12345', [Validators.minLength(5)]],

    })
  }

  public onSubmit(): void {
    let subscription: Subscription;
    subscription = this._authService.resetPassword(this.token!, this.resetPasswordForm?.get('password')?.value).subscribe({
      next: (response) => console.log(response),
      error: (e) => {
        console.log(e);
        subscription.unsubscribe();
      },
      complete: () => subscription.unsubscribe()
    });
  }

}
