{{- define "identra.fullname" -}}
{{- default .Chart.Name .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "identra.labels" -}}
app.kubernetes.io/name: {{ include "identra.fullname" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: identra
{{- end -}}

{{- define "identra.serviceName" -}}
{{- printf "%s-%s" (include "identra.fullname" .root) .name -}}
{{- end -}}
