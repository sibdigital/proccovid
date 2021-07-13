INSERT INTO public.cls_settings (status, key, value, string_value)
VALUES
       (1, 'isAlgorithmSupported', null, 'Подпись выполнена по алгоритму ГОСТ Р 34.10-2012 c длиной ключа 256 или 512 бит'),
       (1, 'isAllCerificateValid', null, 'Сертификаты цепочки действуют'),
       (1, 'isMessageDigestVerify', null, 'Электронная подпись верна'),
       (1, 'isCertificatePathBuild', null, 'Цепочка сертификатов до корневого сертификата построена'),
       (1, 'isCertificatePathNotContainsRevocationCertificate', null, 'Сертификаты цепочки не аннулированы'),
       (1, 'isDataPresent', null, 'Файл с подписанными данными присутствует'),
       (1, 'isSignaturePresent', null, 'Файл с открепленной подпиьсю присутствует'),
       (1, 'isSignedDataReadable', null, 'Входные данные являются подписанным сообщением'),
       (1, 'isCertificatePresent', null, 'Электронная подпись содержит сертификаты'),
       ------------------NO----------------------
       (1, 'isNoAlgorithmSupported', null, 'Неподдерживаемый алгоритм электронной подписи. Подпись должна быть выполнена по алгоритму ГОСТ Р 34.10-2012 c длиной ключа 256 или 512 бит'),
       (1, 'isNoAllCerificateValid', null, 'Срок действия одного из сертификатов цепочки истек или еще не наступил'),
       (1, 'isNoMessageDigestVerify', null, 'Электронная подпись не верна'),
       (1, 'isNoCertificatePathBuild', null, 'Невозможно построить цепочку сертификатов до корневого сертификата'),
       (1, 'isNoCertificatePathNotContainsRevocationCertificate', null, 'Один из сертификатов цепочки аннулирован'),
       (1, 'isNoDataPresent', null, 'Файл с подписанными данными отсуствует или пуст'),
       (1, 'isNoSignaturePresent', null, 'Файл с открепленной подпиьсю отсуствует или пуст'),
       (1, 'isNoSignedDataReadable', null, 'Входные данные не являются подписанным сообщением'),
       (1, 'isNoCertificatePresent', null, 'Электронная подпись содержит сертификаты')
;