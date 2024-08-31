package org.caesar.notificationservice.Data.Services;

import org.caesar.notificationservice.Dto.*;

import java.util.List;
import java.util.UUID;

public interface AdminNotificationService {
    List<AdminNotificationDTO> getAdminNotification(String username);
    boolean sendNotificationAllAdmin(List<SaveAdminNotificationDTO> notification);
    boolean deleteAdminNotification(UUID id);

    List<SaveAdminNotificationDTO> validateDeleteByReport(ReportDTO reportDTO, boolean rollback);
    boolean completeDeleteByReport(ReportDTO reportDTO);
    boolean releaseLock(List<UUID> notificationIds);
    boolean rollbackPreComplete(ReportDTO reportDTO);

    boolean validateOrRollbackDeleteBySupports(SupportDTO support, boolean rollback);
    boolean validateOrRollbackDeleteByReportsOnUserDelete(ReportDTO reportDTO, boolean rollback);

    boolean deleteBySupport(SupportDTO supportDTO);
    boolean updateAdminNotification(List<SaveAdminNotificationDTO> notificationDTO);
    boolean deleteByReport(ReportDTO reportDTO);
}
