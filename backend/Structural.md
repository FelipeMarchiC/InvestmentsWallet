Chegamos no 100% de cobertura de branch em quase todos, exceto no EffectiveWithdrawDateResolver.

| Nome da classe                | No da linha | Justificativa                                                                                                                                          |
| ----------------------------- | ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| EffectiveWithdrawDateResolver | 8           | Aparece como se tivesse coberto 11/12 branches, mas é um if ternário em uma só linha. Acreditamos que possa ter algo relacionado com o LocalDate.now() |
