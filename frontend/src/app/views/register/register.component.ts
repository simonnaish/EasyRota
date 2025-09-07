import {NgIf} from "@angular/common";
import {Component, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {RouterLink} from "@angular/router";
import {TranslatePipe} from "@ngx-translate/core";
import {Subscription} from "rxjs";
import {AuthService} from "src/app/services/auth.service";

@Component({
  selector: 'app-registration',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  imports: [
    FormsModule,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    NgIf,
    TranslatePipe,
    MatButton,
    RouterLink
  ],
  standalone: true
})
export class RegisterComponent implements OnInit {
  private readonly _fb: FormBuilder = inject(FormBuilder);
  private readonly _registerService: AuthService = inject(AuthService);
  public userForm: FormGroup | undefined;


  public ngOnInit(): void {
    this.initUserForm();
  }

  private initUserForm(): void {
    this.userForm = this._fb.group({
      'fullName': ['asasasa', [Validators.required]],
      'email': ['aaaaa@aaaaa', [Validators.minLength(5), Validators.email]],
      'password': ['12345', [Validators.minLength(5)]],
      'confirmPassword': ['12345', [Validators.minLength(5)]],

    })
  }

  public onSubmit(): void {
    let subscription: Subscription;
    subscription = this._registerService.register(this.userForm?.value).subscribe({
      next: (response) => console.log(response),
      error: (e) => {
        console.log(e);
        subscription.unsubscribe();
      },
      complete: () => subscription.unsubscribe()
    });
    // console.log(this.userForm?.value);
  }
}
