{{- define "chart.labels.currentDate" -}}
  {{ now | date "2006-01-02" }}
{{- end }}


{{- define "chart.labels.version" -}}
  {{ .Chart.Version }}
{{- end }}