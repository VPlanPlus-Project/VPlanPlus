# Documentation for the Onboarding feature

During various steps, some key-value pairs will be set in the key value store.
| Key                                | Description                                                   | Only for first profile of school |
| ---------------------------------- | ------------------------------------------------------------- | -------------------------------- |
| onboarding.school_id               | The VPlanPlus ID of the school                                | [ ]                              |
| onboarding.sp24_school_id          | The stundenplan24.de ID of the school                         | [X]                              |
| onboarding.school_name             | The name of the school                                        | [X]                              |
| onboarding.username                | The username of the user                                      | [X]                              |
| onboarding.password                | The password of the user                                      | [X]                              |
| onboarding.days_per_week           | The amount of days per week                                   | [X]                              |
| onboarding.is_fully_supported      | Whether the school is fully supported by VPlanPlus            | [X]                              |
| onboarding.is_first_profile        | Whether this is the first profile for this school             | [ ]                              |
| onboarding.teachers                | A list of teachers, JSON array                                | [ ]                              |
| onboarding.rooms                   | A list of rooms, JSON array                                   | [ ]                              |
| onboarding.classes                 | A list of classes including its number of vpp.IDs, JSON array | [ ]                              |
| onboarding.holidays                | A list of holidays, JSON array                                | [X]                              |
| onboarding.default_lessons         | A list of default lessons, JSON array                         | [ ]                              |
| onboarding.profile_type            | The type of the profile, will get resolved to a ProfileType   | [ ]                              |
| onboarding.profile                 | The name of the entity, e.g. the class name                   | [ ]                              |
| onboarding.profile_default_lessons | A list of default lessons for the profile, JSON array         | [ ]                              |
